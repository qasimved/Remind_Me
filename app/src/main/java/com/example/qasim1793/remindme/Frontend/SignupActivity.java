package com.example.qasim1793.remindme.Frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qasim1793.remindme.Backend.DBHelper;
import com.example.qasim1793.remindme.R;

public class SignupActivity extends AppCompatActivity {
    Button register;
    EditText fullName,email,password,rePassword,cellNo;
    RadioGroup radioSex;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullName = (EditText) findViewById(R.id.fullName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        rePassword = (EditText) findViewById(R.id.rePassword);
        cellNo = (EditText) findViewById(R.id.cellNo);

        radioSex =(RadioGroup) findViewById(R.id.radioSex);



        register = (Button) findViewById(R.id.registerBtn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioSex.getCheckedRadioButtonId()==R.id.male){
                    gender = "Male";
                }
                else{
                    gender = "Female";
                }

                if(fullName.getText().toString().trim().isEmpty()||email.getText().toString().trim().isEmpty()||password.getText().toString().trim().isEmpty()||rePassword.getText().toString().trim().isEmpty()||cellNo.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(SignupActivity.this,"Fill All the Fields First",Toast.LENGTH_LONG).show();
                }
                else if(!password.getText().toString().equals(rePassword.getText().toString())){
                    Toast.makeText(SignupActivity.this,"Password and Repassword are not same",Toast.LENGTH_LONG).show();
                }
                else {
                    DBHelper dbHelper = new DBHelper(SignupActivity.this);
                    if(dbHelper.addUser(fullName.getText().toString().trim(), email.getText().toString().trim(),password.getText().toString().trim(),cellNo.getText().toString().trim(),gender)){
                        Toast.makeText(SignupActivity.this, "User added successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        Toast.makeText(SignupActivity.this, "Error in adding user, User already exist", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


}
