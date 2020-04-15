package com.covid.covidapp.fragments2;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covid.covidapp.R;
import com.covid.covidapp.fragments.FirebaseMethods;
import com.google.android.gms.maps.GoogleMap;

import java.util.Objects;

public class RiskFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private GoogleMap mMap;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RiskFragment() {
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
    public static RiskFragment newInstance(String param1, String param2) {
        RiskFragment fragment = new RiskFragment();
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

    TextView title,message,message2,question;
    Button yes,some,no,pre;
    String[] questions;
    int questionNumber = 0,lastQuestion,totalPoint = 0;
    int[][] points = {{30,20,0},{30,20,0},{20,10,0},{15,10,0},{15,10,0},{15,10,0},{8,4,0},{8,4,0},{8,4,0},{8,4,0},{0,4,8},{8,4,0}};
    int[] answers = new int[12];


    private FirebaseMethods mFirebaseMethods;

    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_risk, container, false);

        userID = getArguments().getString(ARG_PARAM1, getResources().getString(R.string.db_bilinmiyor));
        mFirebaseMethods = new FirebaseMethods(getActivity());
        initViewvButtons(v);
        pre.setVisibility(View.INVISIBLE);
        showQuestios(questions[questionNumber]);



        return v;
    }

    private void initViewvButtons(View v){

        title = (TextView) v.findViewById(R.id.tv_title);
        message = (TextView) v.findViewById(R.id.tv_message);
        message2 = (TextView) v.findViewById(R.id.tv_message2);
        question = (TextView) v.findViewById(R.id.tv_question);
        yes = (Button) v.findViewById(R.id.btn_yes);
        no = (Button) v.findViewById(R.id.btn_no);
        some = (Button) v.findViewById(R.id.btn_som);
        pre = (Button) v.findViewById(R.id.btn_pre);

        questions = getResources().getStringArray(R.array.sorular);
        lastQuestion = getResources().getStringArray(R.array.sorular).length;
        yes.setOnClickListener(this);
        some.setOnClickListener(this);
        no.setOnClickListener(this);
        pre.setOnClickListener(this);

    }

    private void showQuestios(String que){
        question.setText(que);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_yes:
                calculateRisky(0);
                break;
            case R.id.btn_som:
                calculateRisky(1);
                break;
            case R.id.btn_no:
                calculateRisky(2);
                break;
            case R.id.btn_pre:
                questionNumber--;
                if(questionNumber<1){
                    pre.setVisibility(View.INVISIBLE);
                }
                showQuestios(questions[questionNumber]);
                break;
        }
    }

    private void calculateRisky(int answer){
        if(questionNumber>-1){
            pre.setVisibility(View.VISIBLE);
        }
        int point = points[questionNumber][answer];
        answers[questionNumber] = answer;
        questionNumber++;

        totalPoint += point;
        if (questionNumber<(lastQuestion)){
            showQuestios(questions[questionNumber]);
        }
        if (questionNumber==lastQuestion || totalPoint >= 80){
            String riskgorup,riskgroupdb;
            Drawable riskcolor;
            String message;
            if(totalPoint>= 80){
                riskgorup = getResources().getString(R.string.yuksek_riskli);
                riskgroupdb = getResources().getString(R.string.db_yuksek_riskli);
                riskcolor = getResources().getDrawable(R.drawable.background_yuksek);
                message = getResources().getString(R.string.virusmsg2);;
            }else if(totalPoint>=60){
                riskgorup = getResources().getString(R.string.orta_yuksek_riskli);
                riskgroupdb = getResources().getString(R.string.db_orta_yuksek_riskli);
                riskcolor = getResources().getDrawable(R.drawable.background_ortayuksek);
                message = getResources().getString(R.string.virusmsg3);
            }else if(totalPoint>=40){
                riskgorup = getResources().getString(R.string.orta_riskli);
                riskgroupdb = getResources().getString(R.string.db_orta_riskli);
                riskcolor = getResources().getDrawable(R.drawable.background_orta);
                message = getResources().getString(R.string.virusmsg4);
            }else if(totalPoint>=20){
                riskgorup = getResources().getString(R.string.orta_dusuk_riskli);
                riskgroupdb = getResources().getString(R.string.db_orta_dusuk_riskli);
                riskcolor = getResources().getDrawable(R.drawable.background_ortadusuk);
                message = getResources().getString(R.string.virusmsg5);
            }else{
                riskgorup = getResources().getString(R.string.dusuk_riskli);
                riskgroupdb = getResources().getString(R.string.db_dusuk_riskli);
                riskcolor = getResources().getDrawable(R.drawable.background_dusuk);
                message = getResources().getString(R.string.virusmsg1);
            }

            mFirebaseMethods.updateRisk(userID,riskgroupdb);
            showCongratsDialog(riskgorup,riskcolor,message);
        }
    }


    private void showCongratsDialog(final String rg, Drawable rc, String msg){
        final Dialog customDialog = new Dialog(getContext());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setContentView(R.layout.dialog_riskgroup);
        customDialog.setCanceledOnTouchOutside(false);
        final View ssView = (View) customDialog.findViewById(R.id.screenshot);
        TextView title = (TextView) customDialog.findViewById(R.id.tv_title);
        TextView message = (TextView) customDialog.findViewById(R.id.tv_message);
        TextView message2 = (TextView) customDialog.findViewById(R.id.tv_message2);
        TextView message3 = (TextView) customDialog.findViewById(R.id.tv_message3);
        Button yes = (Button) customDialog.findViewById(R.id.btn_yes);
        title.setText(getResources().getString(R.string.result));
        message.setText(getResources().getString(R.string.inforiskgr));
        message2.setText(rg);
        message2.setBackground(rc);
        message3.setText(msg);
        yes.setText(getResources().getString(R.string.contin));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.cancel();
                getActivity().finish();
            }
        });

        customDialog.show();
    }
}
