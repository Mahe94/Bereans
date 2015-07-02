package com.example.mahe.bereans;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.mahe.bereans.util.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AudioFragment extends ListFragment {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list
    private static String url_all_audios = "http://www.bereans.in/mobile_api/audio_list.php";
    private static String PATH = "/Bereans/";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_AUDIO = "audio";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_FILENAME = "filename";

    private static final String URL = "http://bereans.in/audio/";

    // products JSONArray
    JSONArray products = null;

    DownloadManager downloadmanager;
    Intent playerIntent;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    // TODO: Rename and change types and number of parameters
    public static AudioFragment newInstance() {
        AudioFragment fragment = new AudioFragment();
        return fragment;
    }

    public AudioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(((MainActivity) getActivity()).isNetworkAvailable()) {
            String serviceString = Context.DOWNLOAD_SERVICE;
            downloadmanager = (DownloadManager) getActivity().getSystemService(serviceString);

            getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            // Hashmap for ListView
            productsList = new ArrayList<HashMap<String, String>>();

            playerIntent = new Intent();
            playerIntent.setAction(android.content.Intent.ACTION_VIEW);

            // Loading products in Background Thread
            new LoadAllProducts().execute();
        }
        else
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
/**            super.onPreExecute();
 pDialog = new ProgressDialog(AllProductsActivity.this);
 pDialog.setMessage("Loading products. Please wait...");
 pDialog.setIndeterminate(false);
 pDialog.setCancelable(false);
 pDialog.show();
 **/        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_audios, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
/**                // Checking for SUCCESS TAG
 int success = json.getInt(TAG_SUCCESS);

 if (success == 1) {
 **/                    // products found
                // Getting Array of Products
                products = json.getJSONArray(TAG_AUDIO);

                // looping through All Products
                for (int i = 0; i < products.length(); i++) {
                    JSONObject c = products.getJSONObject(i);

                    // Storing each json item in variable
                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_TITLE);
                    String filename = c.getString(TAG_FILENAME);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_ID, id);
                    map.put(TAG_TITLE, name);
                    map.put(TAG_FILENAME, filename);

                    // adding HashList to ArrayList
                    productsList.add(map);
                }
                //               }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
//            pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(), productsList,
                            R.layout.audio_item, new String[]{TAG_ID,
                            TAG_TITLE, TAG_FILENAME},
                            new int[]{R.id.pid, R.id.name, R.id.filename});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        ListView listView = (ListView)view.findViewById(android.R.id.list);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                HashMap<String, String> audio_item = (HashMap<String, String>) av.getItemAtPosition(position);
                downloadAudio(audio_item.get(TAG_FILENAME), audio_item.get(TAG_TITLE));
                return true;
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();

        getActivity().unregisterReceiver(onComplete);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap<String, String> audio_item = (HashMap<String, String>) l.getItemAtPosition(position);

        playerIntent.setDataAndType(Uri.parse(URL + audio_item.get(TAG_FILENAME)), "audio/*");
        startActivity(playerIntent);

        //    downloadAudio(audio_item.get(TAG_FILENAME), audio_item.get(TAG_TITLE));
    }



    public void downloadAudio(String filename, String name) {
        Uri uri = Uri.parse(URL + filename);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        Long reference = downloadmanager.enqueue(new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Downloading Audio")
                .setDescription(name)
                .setShowRunningNotification(true)
                .setDestinationInExternalPublicDir(PATH , filename));;
//        Toast.makeText(getActivity(), "Downloading " + name, Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = downloadmanager.query(q);

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    /** process download
                    Toast.makeText(getActivity(), c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI)) + " " + c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)) + " " + c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)), Toast.LENGTH_LONG).show();
                     get other required data by changing the constant passed to getColumnIndex


                     Intent playerIntent = new Intent();
                     playerIntent.setAction(android.content.Intent.ACTION_VIEW);
                     File file = new File(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
                     playerIntent.setDataAndType(Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI))), "audio/*");
                     startActivity(playerIntent);
**/
                    playerIntent.setDataAndType(Uri.parse(URL + c.getString(c.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))), "audio/*");
                    PendingIntent pendingPlayerIntent = PendingIntent.getActivity(ctxt, 0, playerIntent, 0);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getActivity())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Download complete")
                                    .setContentText(c.getString(c.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)))
                                    .setContentIntent(pendingPlayerIntent)
                                    .setAutoCancel(true);
                    int mNotificationId = 001;
                // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());

//                    Toast.makeText(getActivity(), "Notification", Toast.LENGTH_LONG).show();
                 }
            }
        }
    };
}
