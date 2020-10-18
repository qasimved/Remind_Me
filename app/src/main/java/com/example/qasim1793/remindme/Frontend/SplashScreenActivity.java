package com.example.qasim1793.remindme.Frontend;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.qasim1793.remindme.R;
import com.example.qasim1793.remindme.Utils.SharedPrefManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(SplashScreenActivity.this);
                if(sharedPrefManager.getStringVar("_id").isEmpty()){
                    Intent startActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(startActivity);
                    finish();
                }
                else {
                    Intent startActivity = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                    startActivity(startActivity);
                    finish();
                }

            }
        }, 5000);
    }
}
