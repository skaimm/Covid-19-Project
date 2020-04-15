package com.covid.covidapp.fragments2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.covid.covidapp.LoadingDialog;
import com.covid.covidapp.R;
import com.covid.covidapp.activities.MapActivity;
import com.covid.covidapp.activities.ProfilActivity;
import com.covid.covidapp.activities.RiskActivity;
import com.covid.covidapp.activities.StatActivity;
import com.covid.covidapp.fragments.FirebaseMethods;
import com.covid.covidapp.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.Objects;

public class FirstFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private GoogleMap mMap;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment() {
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
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
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

    private ImageView info;
    private CardView risk,test,map,stat,voluntary,profil;
    private String meslek = "secilmedi";



    private String userID;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference myRef;

    LoadingDialog loadingDialog;
    private User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        init(v);

        loadingDialog= new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        userID = getArguments().getString(ARG_PARAM1, getResources().getString(R.string.db_bilinmiyor));
        mFirebaseMethods = new FirebaseMethods(getActivity());

        setupFirebaseAuth();

        return v;
    }
    public void init(View v){
        mUser = new User();
        risk = v.findViewById(R.id.card_risk);
        test = v.findViewById(R.id.card_test);
        voluntary = v.findViewById(R.id.card_voluntary);
        map = v.findViewById(R.id.card_map);
        profil = v.findViewById(R.id.card_profil);
        stat = v.findViewById(R.id.card_stat);

        info = v.findViewById(R.id.im_info);

        stat.setOnClickListener(this);
        profil.setOnClickListener(this);
        risk.setOnClickListener(this);
        map.setOnClickListener(this);
        info.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_risk:
                openActivity(RiskActivity.class);
                break;
            case R.id.card_map:
                openActivity(MapActivity.class);
                break;
            case R.id.card_profil:
                openActivity(ProfilActivity.class);
                break;
            case R.id.card_stat:
                openActivity(StatActivity.class);
                break;
            case R.id.im_info:
                showInfoDialog();
                break;
        }
    }
    public void openActivity(Class activity){
        Intent intent = new Intent(getActivity(),activity);
        startActivity(intent);
    }


    private void showCongratsDialog(String ttl,String msg){
        final Dialog customDialog = new Dialog(Objects.requireNonNull(getContext()));
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setContentView(R.layout.dialog_congrats);
        customDialog.setCanceledOnTouchOutside(false);
        TextView title = (TextView) customDialog.findViewById(R.id.tv_title);
        TextView message = (TextView) customDialog.findViewById(R.id.tv_message);
        Button yes = (Button) customDialog.findViewById(R.id.btn_yes);

        title.setText(ttl);
        message.setText(msg);
        yes.setText(getResources().getString(R.string.okay));

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.cancel();
            }
        });

        customDialog.show();
    }

    private void showInfoDialog(){
        final Dialog customDialog = new Dialog(Objects.requireNonNull(getContext()));
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setContentView(R.layout.dialog_help);
        customDialog.setCanceledOnTouchOutside(false);
        Button yes = (Button) customDialog.findViewById(R.id.btn_yes);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.cancel();
            }
        });
        customDialog.show();
    }


    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
                // ...
            }
        };

        myRef.child(getResources().getString(R.string.db_users)).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);

                loadingDialog.dissmissDialog();
                voluntary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(mUser.getGonul().equals(getResources().getString(R.string.db_bilinmiyor))){
                            final Dialog customDialog = new Dialog(Objects.requireNonNull(getContext()));
                            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialog.setContentView(R.layout.layout_gonul);
                            customDialog.setCanceledOnTouchOutside(false);
                            PowerSpinnerView spinner = (PowerSpinnerView) customDialog.findViewById(R.id.spinner_meslek);
                            TextView title = (TextView) customDialog.findViewById(R.id.tv_title);
                            final TextView message = (TextView) customDialog.findViewById(R.id.tv_message);
                            Button yes = (Button) customDialog.findViewById(R.id.btn_yes);
                            Button no = (Button) customDialog.findViewById(R.id.btn_no);

                            title.setText(getResources().getString(R.string.bevoluntary));
                            message.setText(getResources().getString(R.string.gonuldiatext) + "\n" + getResources().getString(R.string.gonuldiatext2));
                            yes.setText(getResources().getString(R.string.okay));
                            no.setText(getResources().getString(R.string.notnow));
                            spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
                                @Override public void onItemSelected(int position, String item) {
                                    meslek = item;
                                }
                            });
                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(meslek.equals("secilmedi")){
                                        final Dialog customDialog2 = new Dialog(getContext());
                                        customDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        Objects.requireNonNull(customDialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        customDialog2.setContentView(R.layout.dialog_congrats);
                                        customDialog2.setCanceledOnTouchOutside(false);
                                        TextView title = (TextView) customDialog2.findViewById(R.id.tv_title);
                                        TextView message = (TextView) customDialog2.findViewById(R.id.tv_message);
                                        Button yes = (Button) customDialog2.findViewById(R.id.btn_yes);

                                        title.setText(getResources().getString(R.string.warning));
                                        message.setText(getResources().getString(R.string.warmeslek));
                                        yes.setText(getResources().getString(R.string.okay));

                                        yes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                customDialog2.cancel();
                                            }
                                        });
                                        customDialog2.show();
                                    }else{
                                        customDialog.cancel();
                                        String info;
                                        if(meslek.equals(getResources().getString(R.string.item_saglik))){
                                            info = getResources().getString(R.string.item_saglik);
                                            meslek = getResources().getString(R.string.db_item_saglik);
                                        }else if(meslek.equals(getResources().getString(R.string.item_milli))){
                                            info = getResources().getString(R.string.item_milli);
                                            meslek = getResources().getString(R.string.db_item_milli);
                                        }else if(meslek.equals(getResources().getString(R.string.item_guvenlik))){
                                            info = getResources().getString(R.string.item_guvenlik);
                                            meslek = getResources().getString(R.string.db_item_guvenlik);
                                        }else{
                                            info = getResources().getString(R.string.textgonuldiger);
                                            meslek = getResources().getString(R.string.db_item_diger);
                                        }

                                        showCongratsDialog(getResources().getString(R.string.thanks),getResources().getString(R.string.textgonul) + " " +info);
                                        mFirebaseMethods.updateGonul(userID,meslek);
                                    }
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    meslek = "secilmedi";
                                    customDialog.cancel();
                                }
                            });

                            customDialog.show();
                        }else{
                            final Dialog customDialog = new Dialog(Objects.requireNonNull(getContext()));
                            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialog.setContentView(R.layout.layout_market);
                            customDialog.setCanceledOnTouchOutside(false);
                            TextView title = (TextView) customDialog.findViewById(R.id.tv_title);
                            TextView message = (TextView) customDialog.findViewById(R.id.tv_message);
                            Button yes = (Button) customDialog.findViewById(R.id.btn_yes);
                            Button no = (Button) customDialog.findViewById(R.id.btn_no);

                            title.setText(getResources().getString(R.string.bevoluntary));
                            message.setText(getResources().getString(R.string.gonulgiveup));
                            yes.setText(getResources().getString(R.string.yes));
                            no.setText(getResources().getString(R.string.no));
                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showCongratsDialog(getResources().getString(R.string.bevoluntary),getResources().getString(R.string.gonulgiveupmsg));
                                    meslek = "secilmedi";
                                    mFirebaseMethods.updateGonul(userID,getResources().getString(R.string.db_bilinmiyor));
                                    customDialog.cancel();
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    customDialog.cancel();
                                }
                            });

                            customDialog.show();

                        }

                    }
                });
                test.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mUser.getTest().equals(getResources().getString(R.string.db_bilinmiyor))){
                            final Dialog customDialog = new Dialog(getContext());
                            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialog.setContentView(R.layout.layout_test);
                            customDialog.setCanceledOnTouchOutside(false);
                            TextView title = (TextView) customDialog.findViewById(R.id.tv_title);
                            TextView message = (TextView) customDialog.findViewById(R.id.tv_message);
                            Button yes = (Button) customDialog.findViewById(R.id.btn_yes);
                            Button no = (Button) customDialog.findViewById(R.id.btn_no);
                            Button okay = (Button) customDialog.findViewById(R.id.btn_okay);

                            title.setText(getResources().getString(R.string.betest));
                            message.setText(getResources().getString(R.string.entertextresult));
                            yes.setText(getResources().getString(R.string.pozitif));
                            no.setText(getResources().getString(R.string.negatif));
                            okay.setText(getResources().getString(R.string.exit));

                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mFirebaseMethods.updateTest(userID,getResources().getString(R.string.db_pozitif));
                                    String riskgr = getResources().getString(R.string.db_yuksek_riskli);
                                    mFirebaseMethods.updateRisk(userID,riskgr);
                                    customDialog.cancel();
                                    showCongratsDialog(getResources().getString(R.string.thanks),getResources().getString(R.string.enteredtextresult));
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mFirebaseMethods.updateTest(userID,getResources().getString(R.string.db_negatif));
                                    String riskgr = getResources().getString(R.string.db_dusuk_riskli);
                                    mFirebaseMethods.updateRisk(userID,riskgr);
                                    customDialog.cancel();
                                    showCongratsDialog(getResources().getString(R.string.thanks),getResources().getString(R.string.enteredtextresult));
                                }
                            });
                            okay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    customDialog.cancel();
                                }
                            });
                            customDialog.show();

                        }else{
                            final Dialog customDialog = new Dialog(getContext());
                            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialog.setContentView(R.layout.layout_market);
                            customDialog.setCanceledOnTouchOutside(false);
                            TextView title = (TextView) customDialog.findViewById(R.id.tv_title);
                            TextView message = (TextView) customDialog.findViewById(R.id.tv_message);
                            Button yes = (Button) customDialog.findViewById(R.id.btn_yes);
                            Button no = (Button) customDialog.findViewById(R.id.btn_no);

                            title.setText(getResources().getString(R.string.betest));
                            message.setText(getResources().getString(R.string.testgiveup));
                            yes.setText(getResources().getString(R.string.yes));
                            no.setText(getResources().getString(R.string.no));
                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mFirebaseMethods.updateTest(userID,getResources().getString(R.string.db_bilinmiyor));
                                    showCongratsDialog(getResources().getString(R.string.betest), getResources().getString(R.string.testchanged) + " " + getResources().getString(R.string.bilinmiyor));
                                    openActivity(RiskActivity.class);
                                    customDialog.cancel();
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    customDialog.cancel();
                                }
                            });
                            customDialog.show();
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
