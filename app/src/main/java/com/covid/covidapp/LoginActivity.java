package com.covid.covidapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.covid.covidapp.activities.FirstActivity;
import com.covid.covidapp.fragments.FirebaseMethods;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 111;
    private Button login;
    private EditText adsoyad;
    private Context mContext;
    private String isim;
    private FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.newLogger(getApplicationContext(), getResources().getString(R.string.facebook_application_id));

        mContext = LoginActivity.this;

        adsoyad = findViewById(R.id.input_fullname);
        login = findViewById(R.id.btn_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isim = adsoyad.getText().toString();
                isim = isim.trim();
                if(isim.isEmpty()){
                    final Dialog customDialog2 = new Dialog(mContext);
                    customDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Objects.requireNonNull(customDialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    customDialog2.setContentView(R.layout.dialog_congrats);
                    customDialog2.setCanceledOnTouchOutside(false);
                    TextView title = (TextView) customDialog2.findViewById(R.id.tv_title);
                    TextView message = (TextView) customDialog2.findViewById(R.id.tv_message);
                    Button yes = (Button) customDialog2.findViewById(R.id.btn_yes);

                    title.setText(getResources().getString(R.string.warning));
                    message.setText(getResources().getString(R.string.checkname));
                    yes.setText(getResources().getString(R.string.okay));

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            customDialog2.cancel();
                        }
                    });
                    customDialog2.show();
                }else{
                    showLogin();
                }
            }
        });
    }

    private void showLogin(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                firebaseMethods = new FirebaseMethods(mContext);
                String number,email;
                if(user.getPhoneNumber() != null){
                    number = user.getPhoneNumber();
                }else{
                    number = getResources().getString(R.string.db_bilinmiyor);
                }
                if(user.getEmail() != null){
                    email = user.getEmail();
                }else{
                    email = getResources().getString(R.string.db_bilinmiyor);
                }
                firebaseMethods.addNewUser(isim,number,email,getResources().getString(R.string.db_bilinmiyor),getResources().getString(R.string.db_bilinmiyor),
                        getResources().getString(R.string.db_bilinmiyor),getResources().getString(R.string.db_bilinmiyor),getResources().getString(R.string.db_bilinmiyor),0.0,0.0);

                Intent intent = new Intent(this, FirstActivity.class);
                startActivity(intent);
                finish();

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }



}
