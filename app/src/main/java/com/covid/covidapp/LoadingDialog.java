package com.covid.covidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {

    Activity activity;
    AlertDialog dialog;

    public LoadingDialog(Activity mActivity){
        activity = mActivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dissmissDialog(){
        dialog.dismiss();
    }
}
