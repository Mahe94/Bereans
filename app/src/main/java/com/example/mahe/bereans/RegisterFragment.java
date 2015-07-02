package com.example.mahe.bereans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    GoogleCloudMessaging gcm;
    Context context;
    String regId;
    EditText editText_password;
    EditText editText_username;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param firstPageFragmentListener
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        editText_username = (EditText) view.findViewById(R.id.username);
        editText_password = (EditText) view.findViewById(R.id.password);

        Button register_button = (Button) view.findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration();
            }
        });

        return view;
    }


    public void registration() {
        String username = editText_username.getText().toString();
        if(!username.isEmpty()) {
            String password = editText_password.getText().toString();
            if (password.equals("BEREANS")) {
                if(((MainActivity) getActivity()).isNetworkAvailable()) {
                    Toast.makeText(getActivity(), "reached registration", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
                    intent.putExtra("username", username);
                    getActivity().startService(intent);
                }
                else
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "INVALID PASSWORD. Please contact Bijo Thomas to get your password", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(context, "Enter a Username", Toast.LENGTH_LONG).show();
        }
    }

}
