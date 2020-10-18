package com.example.qasim1793.remindme.Frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qasim1793.remindme.R;

public class SkipActivity extends AppCompatActivity {
    Button registerHere ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip);

        registerHere = (Button) findViewById(R.id.registerBtn);
        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SkipActivity.this,SignupActivity.class));
                finish();
            }
        });
    }


}
