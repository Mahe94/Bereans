package com.example.mahe.bereans;

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


public class NewsFragment extends ListFragment {

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list
    private static String url_all_news = "http://www.bereans.in/mobile_api/news_list.php";

    // JSON Node names
    private static final String TAG_NEWS = "news";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";

    // products JSONArray
    JSONArray products = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        productsList = new ArrayList<HashMap<String, String>>();
        // Loading products in Background Thread

        if(((MainActivity) getActivity()).isNetworkAvailable())
            new LoadAllProducts().execute();
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
            JSONObject json = jParser.makeHttpRequest(url_all_news, "GET", params);

            // Check your log cat for JSON reponse
//            Log.d("All Products: ", json.toString());

            try {
/**                // Checking for SUCCESS TAG
 int success = json.getInt(TAG_SUCCESS);

 if (success == 1) {
 **/                    // products found
                // Getting Array of Products
                products = json.getJSONArray(TAG_NEWS);

                // looping through All Products
                for (int i = 0; i < products.length(); i++) {
                    JSONObject c = products.getJSONObject(i);

                    // Storing each json item in variable
                    String title = c.getString(TAG_TITLE);
                    String description = c.getString(TAG_DESCRIPTION);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_TITLE, title);
                    map.put(TAG_DESCRIPTION, description);

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
                            R.layout.news_item, new String[]{TAG_TITLE, TAG_DESCRIPTION},
                            new int[]{R.id.news_title, R.id.news_description});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


}
