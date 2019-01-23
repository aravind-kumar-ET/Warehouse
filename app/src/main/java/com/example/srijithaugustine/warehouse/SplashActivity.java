package com.example.srijithaugustine.warehouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity  {
    private int progressStatus = 0;
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 100){
                    progressStatus ++ ;
                    android.os.SystemClock.sleep(50);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }});
                }
            }
        }).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("RegisterationDetails",MODE_PRIVATE);
                boolean result = sp.getBoolean("IsRegistered",false);
                if(result){
                    Intent homeintent = new Intent(SplashActivity.this,PickupNoteActivity.class);
                    startActivity(homeintent);
                    finish();
                }
                else{
                    Intent homeintent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(homeintent);
                    finish();
                }

            }},5000);
    }
}
