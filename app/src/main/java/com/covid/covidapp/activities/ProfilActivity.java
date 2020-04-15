package com.covid.covidapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.covid.covidapp.LoginActivity;
import com.covid.covidapp.R;
import com.covid.covidapp.fragments.MarketFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ProfilActivity extends AppCompatActivity {


    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        controlInternetConnection();
    }

    private void controlInternetConnection(){
        if(haveNetWork()){
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                openFragment(MarketFragment.newInstance(userID,""));
            }
        }else {

            final Dialog customDialog2 = new Dialog(this);
            customDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(customDialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            customDialog2.setContentView(R.layout.dialog_congrats);
            customDialog2.setCanceledOnTouchOutside(false);
            TextView title = (TextView) customDialog2.findViewById(R.id.tv_title);
            TextView message = (TextView) customDialog2.findViewById(R.id.tv_message);
            Button yes = (Button) customDialog2.findViewById(R.id.btn_yes);

            title.setText(getResources().getString(R.string.warning));
            message.setText(getResources().getString(R.string.checkcon));
            yes.setText(getResources().getString(R.string.okay));

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controlInternetConnection();
                }
            });
            customDialog2.show();
        }
    }

    private boolean haveNetWork(){
        boolean have_WIFI = false;
        boolean have_Mobıl = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo infos: networkInfos){
            if(infos.getTypeName().equalsIgnoreCase("WIFI")){
                if(infos.isConnected()){
                    have_WIFI =true;
                }
            }
            if(infos.getTypeName().equalsIgnoreCase("MOBILE")){
                if(infos.isConnected()){
                    have_Mobıl =true;
                }
            }
        }
        return have_Mobıl||have_WIFI;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
