package com.covid.covidapp.fragments;

import android.content.Context;

import com.covid.covidapp.R;
import com.covid.covidapp.models.User;
import com.covid.covidapp.models.UserLoc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseMethods {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String userID;
    private Context mContext;
    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() !=null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void addNewUser(String name, String phone , String email,String country, String city, String test, String risk, String gonul, double lat, double lng ){
        User user = new User(userID,name,phone,email,test,risk,gonul);
        myRef.child(mContext.getString(R.string.db_users)).child(userID).setValue(user);

        UserLoc location = new UserLoc(lat,lng,country,city,risk);
        myRef.child(mContext.getString(R.string.db_location)).child(userID).setValue(location);
    }


    public void updateGonul(String key,String answer) {
        myRef.child(mContext.getString(R.string.db_users)).child(key).child(mContext.getString(R.string.field_gonul)).setValue(answer);
    }
    public void updateTest(String key,String answer) {
        myRef.child(mContext.getString(R.string.db_users)).child(key).child(mContext.getString(R.string.field_test)).setValue(answer);
    }
    public void updateRisk(String key,String answer) {
        myRef.child(mContext.getString(R.string.db_users)).child(key).child(mContext.getString(R.string.field_risk)).setValue(answer);
        myRef.child(mContext.getString(R.string.db_location)).child(key).child(mContext.getString(R.string.field_risk)).setValue(answer);
    }

    public void updateUserLocationLong(String key, double lat, double lng,String country,String city) {
        myRef.child(mContext.getString(R.string.db_location)).child(key).child(mContext.getString(R.string.field_lat)).setValue(lat);
        myRef.child(mContext.getString(R.string.db_location)).child(key).child(mContext.getString(R.string.field_lng)).setValue(lng);
        myRef.child(mContext.getString(R.string.db_location)).child(key).child(mContext.getString(R.string.field_country)).setValue(country);
        myRef.child(mContext.getString(R.string.db_location)).child(key).child(mContext.getString(R.string.field_city)).setValue(city);
    }
}
