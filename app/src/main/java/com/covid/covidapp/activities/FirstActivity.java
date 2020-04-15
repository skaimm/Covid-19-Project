package com.covid.covidapp.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.covid.covidapp.fragments2.FirstFragment;
import com.covid.covidapp.LoadingDialog;
import com.covid.covidapp.LoginActivity;
import com.covid.covidapp.R;
import com.covid.covidapp.fragments.FirebaseMethods;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FirstActivity extends AppCompatActivity{


    private static final int REQUEST_CODE = 1;
    String userID;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        loadingDialog= new LoadingDialog(this);
        loadingDialog.startLoadingDialog();


        controlInternetConnection();
    }

    private void controlInternetConnection(){
        if(haveNetWork()){
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                controlLocationPermit();
                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                openFragment(FirstFragment.newInstance(userID,""));
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

    private void controlLocationPermit(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirstActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }else{
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getCurrentLocation(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(FirstActivity.this).requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(FirstActivity.this).removeLocationUpdates(this);
                if(locationResult != null && locationResult.getLocations().size()>0){
                    int latestLocationIndex = locationResult.getLocations().size() -1;
                    double lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double lng = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                    FirebaseMethods firebaseMethods = new FirebaseMethods(FirstActivity.this);


                    Geocoder geocoder = new Geocoder(FirstActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(lat, lng, 1);

                        String cityName = addresses.get(0).getAdminArea();
                        String countryName = addresses.get(0).getCountryCode();
                        Locale name = new Locale.Builder().setRegion(countryName).build();
                        firebaseMethods.updateUserLocationLong(userID,lat,lng,name.getISO3Country(),cityName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    loadingDialog.dissmissDialog();

                }
            }
        }, Looper.getMainLooper());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
