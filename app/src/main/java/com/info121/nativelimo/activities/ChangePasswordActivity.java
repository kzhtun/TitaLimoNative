package com.info121.nativelimo.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.ObjectRes;
import com.info121.nativelimo.models.RequestUpdatePassword;
import com.info121.nativelimo.utils.PrefDB;
import com.info121.nativelimo.utils.Util;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    PrefDB prefDB;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.new_password)
    EditText mNewPassword;

    @BindView(R.id.confirm_password)
    EditText mConfirmPassword;


    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefDB = new PrefDB(getApplicationContext());

        setContentView(R.layout.activity_change_password);

        mContext = ChangePasswordActivity.this;

        ButterKnife.bind(this);

        // set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Change Password");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @OnClick(R.id.update_password)
    public void UpdatePassword_Onclick(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Change Password");

        if(mNewPassword.getText().toString().length()==0){
            alertDialog.setMessage("Passwords should not be left blank!");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog.show();
        }

        if(mNewPassword.getText().toString().equals(mConfirmPassword.getText().toString())){
            callUpdatePassword();
        }else{

            alertDialog.setMessage("Passwords do not match! ");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog.show();
        }
    }


    private void callUpdatePassword(){
        Call<ObjectRes> call = RestClient.COACH().getApiService().UpdatePassword(
                new RequestUpdatePassword(
                        mNewPassword.getText().toString().trim())
        );

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {
                assert response.body() != null;
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setTitle("Change Password");
                    alertDialog.setMessage("Password changed successfully!\nPlease login with new password again.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mNewPassword.setText("");
                            mConfirmPassword.setText("");

                            prefDB.putString(App.CONST_PASSWORD, "");
                            finishAffinity();
                            startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                        }
                    });

                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("Change Password");
                alertDialog.setMessage("Error in password changing!");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle Action bar item clicks here. The Action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}