package com.covid.covidapp.fragments2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.covid.covidapp.R;
import com.covid.covidapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProfileFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
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
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    private TextView tv_name,tv_city,tv_rg,tv_rg2,tv_ts,tv_ts2,tv_info;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String userID;
    private User mUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        init_Textes(v);

        mUser = new User();
        userID = getArguments().getString(ARG_PARAM1, "bilinmiyor");

        setupFirebaseAuth();
        return v;
    }

    void init_Textes(View v){
        tv_name = v.findViewById(R.id.tv_adsoyad);
        tv_city = v.findViewById(R.id.tv_location);
        tv_rg = v.findViewById(R.id.tv_riskgroup);
        tv_rg2 = v.findViewById(R.id.tv_riskgroup2);
        tv_ts = v.findViewById(R.id.tv_testsonuc);
        tv_ts2 = v.findViewById(R.id.tv_testsonuc2);
        tv_info = v.findViewById(R.id.tv_infovol);
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        myRef.child(getResources().getString(R.string.db_location)).child(userID).child(getResources().getString(R.string.field_city)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String city = dataSnapshot.getValue(String.class);
                tv_city.setText(city);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child(getResources().getString(R.string.db_users)).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUser = dataSnapshot.getValue(User.class);
                tv_name.setText(mUser.getIsim());


                String risk = mUser.getRisk();

                if(risk.equals(getResources().getString(R.string.db_yuksek_riskli))){

                    tv_rg2.setText(getResources().getString(R.string.yuksek_riskli));
                    tv_rg2.setBackground(getResources().getDrawable(R.drawable.background_yuksek));

                }else if(risk.equals(getResources().getString(R.string.db_orta_yuksek_riskli))){

                    tv_rg2.setText(getResources().getString(R.string.orta_yuksek_riskli));
                    tv_rg2.setBackground(getResources().getDrawable(R.drawable.background_ortayuksek));

                }else if(risk.equals(getResources().getString(R.string.db_orta_riskli))){

                    tv_rg2.setText(getResources().getString(R.string.orta_riskli));
                    tv_rg2.setBackground(getResources().getDrawable(R.drawable.background_orta));

                }else if(risk.equals(getResources().getString(R.string.db_orta_dusuk_riskli))){

                    tv_rg2.setText(getResources().getString(R.string.orta_dusuk_riskli));
                    tv_rg2.setBackground(getResources().getDrawable(R.drawable.background_ortadusuk));

                }else if(risk.equals(getResources().getString(R.string.db_dusuk_riskli))){

                    tv_rg2.setText(getResources().getString(R.string.dusuk_riskli));
                    tv_rg2.setBackground(getResources().getDrawable(R.drawable.background_dusuk));

                }else{

                    tv_rg2.setText(getResources().getString(R.string.bilinmiyor));
                    tv_rg2.setBackground(getResources().getDrawable(R.drawable.background_bilinmiyor));
                }

                String test = mUser.getTest();

                if(test.equals(getResources().getString(R.string.db_pozitif))){
                    tv_ts2.setText(getResources().getString(R.string.pozitif));
                    tv_ts2.setBackground(getResources().getDrawable(R.drawable.background_yuksek));
                }else if(test.equals(getResources().getString(R.string.db_negatif))){
                    tv_ts2.setText(getResources().getString(R.string.negatif));
                    tv_ts2.setBackground(getResources().getDrawable(R.drawable.background_dusuk));
                }else{
                    tv_ts2.setText(getResources().getString(R.string.bilinmiyor));
                    tv_ts2.setBackground(getResources().getDrawable(R.drawable.background_bilinmiyor));
                }


                if(mUser.getGonul().equals(getResources().getString(R.string.db_item_saglik))){
                    tv_info.setText(getResources().getString(R.string.textgonul) + " " +getResources().getString(R.string.item_saglik));
                }else if(mUser.getGonul().equals(getResources().getString(R.string.db_item_milli))){
                    tv_info.setText(getResources().getString(R.string.textgonul) + " " +getResources().getString(R.string.item_milli));
                }else if(mUser.getGonul().equals(getResources().getString(R.string.db_item_guvenlik))){
                    tv_info.setText(getResources().getString(R.string.textgonul) + " " +getResources().getString(R.string.item_guvenlik));
                }else if(mUser.getGonul().equals(getResources().getString(R.string.db_diger))){
                    tv_info.setText(getResources().getString(R.string.textgonul) + " " +getResources().getString(R.string.textgonuldiger));
                }else{
                    tv_info.setText(getResources().getString(R.string.gonullu_degil));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
