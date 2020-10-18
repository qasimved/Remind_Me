package com.example.qasim1793.remindme.Frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qasim1793.remindme.Backend.DBHelper;
import com.example.qasim1793.remindme.R;

public class LoginActivity extends AppCompatActivity {
    TextView signupText,skip_text;
    Button login;
    EditText email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        signupText = (TextView) findViewById(R.id.signup_text);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        login = (Button) findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(LoginActivity.this);
                if(dbHelper.login(email.getText().toString(),password.getText().toString())){
                    Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"User Not Found",Toast.LENGTH_LONG).show();
                }

            }
        });

        skip_text = (TextView) findViewById(R.id.skip_text);
        skip_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SkipActivity.class);
                startActivity(intent);
//                finish();
            }
        });

    }
}