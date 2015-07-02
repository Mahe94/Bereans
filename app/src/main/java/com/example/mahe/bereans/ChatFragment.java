package com.example.mahe.bereans;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG = "ChatFragment";


    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private String regId;
    private String message;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        regId = sharedPreferences.getString(QuickstartPreferences.SENT_TOKEN_TO_SERVER, "");

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.chat_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 refreshContent();
             }
         });

        buttonSend = (Button) view.findViewById(R.id.buttonChatSend);


        listView = (ListView) view.findViewById(R.id.chatView);

        chatArrayAdapter = new ChatArrayAdapter(getActivity(), R.layout.chat_singlemessage);
        listView.setAdapter(chatArrayAdapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);

        chatText = (EditText) view.findViewById(R.id.chatText);
/**
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
      **/

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                message = chatText.getText().toString();
                sendChatMessage(message);
            }
        });

        chatArrayAdapter.addChatList();

        return view;
    }

    public void sendChatMessage(String message){
        if(((MainActivity) getActivity()).isNetworkAvailable()) {
            Intent intent = new Intent(getActivity(), SendChatIntentService.class);
            intent.putExtra("regId", regId);
            intent.putExtra("message", message);
            getActivity().startService(intent);


            chatArrayAdapter.add(new ChatMessage(false, message, "Me"));
            chatArrayAdapter.addToDb(chatText.getText().toString(), "Me");
        }
        else
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
        chatText.setText("");
    }

    public void getChatMessage(String message, String name) {
        chatArrayAdapter.add(new ChatMessage(true, message, name));
//        chatArrayAdapter.addToDb(message, name);                  //already handled in MyGcmListenerService
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    private void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chatArrayAdapter.loadMoredata();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 0);
    }
}
