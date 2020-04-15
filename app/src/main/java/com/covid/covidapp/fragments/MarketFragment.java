package com.covid.covidapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.covid.covidapp.R;
import com.covid.covidapp.fragments2.ProfileFragment;

public class MarketFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MarketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarketFragment newInstance(String param1, String param2) {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    //private Element adsElement;
    Button btn_profile,btn_about;
    View view;
    private String userID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_market, container, false);

        userID = getArguments().getString(ARG_PARAM1, getResources().getString(R.string.db_bilinmiyor));

        view = v.findViewById(R.id.view2);
        btn_profile = v.findViewById(R.id.btn_profile);
        btn_about = v.findViewById(R.id.btn_about);

        btn_profile.setOnClickListener(this);
        btn_about.setOnClickListener(this);
        btn_profile.setBackground(getResources().getDrawable(R.drawable.background_text));
        openFragment(ProfileFragment.newInstance(userID, ""));

     return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_profile:
                btn_about.setBackground(getResources().getDrawable(R.drawable.background_button));
                btn_profile.setBackground(getResources().getDrawable(R.drawable.background_text));
                view.setVisibility(View.VISIBLE);
                openFragment(ProfileFragment.newInstance(userID, ""));
                break;
            case R.id.btn_about:
                btn_profile.setBackground(getResources().getDrawable(R.drawable.background_button));
                btn_about.setBackground(getResources().getDrawable(R.drawable.background_text));
                view.setVisibility(View.INVISIBLE);
                openFragment(AboutFragment.newInstance("", ""));
                break;
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.container_abopro, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
