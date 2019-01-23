package com.example.srijithaugustine.warehouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView info;
    private Button loginBtn;
    ProgressDialog progressDialog;

    protected GeneralFN GFN = new GeneralFN();
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        info = (TextView) findViewById(R.id.tvinfo);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(LoginActivity.this, android.R.style.Theme_Material_Light_Dialog);
                        progressDialog.setMessage("Please Wait..!");
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();

                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        attemptSignIn();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progressDialog.dismiss();
                    }
                };*/


                startActivity(new Intent(LoginActivity.this, PickupNoteActivity.class));
                finish();
            }
        });
    }

    private void attemptSignIn() {
        if (TextUtils.isEmpty(username.getText())) {
            username.setError("Please enter your user name");
            return;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError("please enter your password");
            return;
        }
        new AsyncTask<Void, Void, GeneralFN.LoginData>() {


            @Override
            protected GeneralFN.LoginData doInBackground(Void... voids) {
                GeneralFN.LoginData loginData = GFN.Login(username.getText().toString(), password.getText().toString(),getApplicationContext());
                return loginData;
            }

            @Override
            protected void onPostExecute(GeneralFN.LoginData loginData) {
                SharedPreferences sp = getSharedPreferences("RegisterationDetails", MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                /*if (loginData.isRegistered()) {
                    edit.putBoolean("IsRegistered", loginData.isRegistered());
                    edit.putString("User_Id", loginData.getUserID());
                    edit.apply();
                    startActivity(new Intent(LoginActivity.this, PickupNoteActivity.class));
                    finish();
                } else {
                    edit.putBoolean("IsRegistered", loginData.isRegistered());
                    edit.putString("User_Id", loginData.getUserID());
                    edit.apply();
                    Toast.makeText(getApplicationContext(), loginData.getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                    finish();
                }*/

            }
        }.execute();
    }
}
