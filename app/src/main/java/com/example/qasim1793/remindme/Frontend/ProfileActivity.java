package com.example.qasim1793.remindme.Frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.qasim1793.remindme.Backend.DBHelper;
import com.example.qasim1793.remindme.R;
import com.example.qasim1793.remindme.Utils.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {
    Button edit,cancel,changePassword;
    EditText fullName,email,cellNo;
    RadioGroup radioSex;
    String gender;
    RadioButton male,female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullName = (EditText) findViewById(R.id.fullName);
        email = (EditText) findViewById(R.id.email);
        cellNo = (EditText) findViewById(R.id.cellNo);

        radioSex = (RadioGroup) findViewById(R.id.radioSex);

        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);


        final SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        fullName.setText(sharedPrefManager.getStringVar("fullName"));
        email.setText(sharedPrefManager.getStringVar("email"));
        cellNo.setText(sharedPrefManager.getStringVar("cellNo"));
            if(sharedPrefManager.getStringVar("gender").equalsIgnoreCase("male")){
                male.setChecked(true);
            }
            else {
                female.setChecked(true);

           }


        edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioSex.getCheckedRadioButtonId() == R.id.male) {
                    gender = "Male";
                } else {
                    gender = "Female";
                }

                if (fullName.getText().toString().trim().isEmpty() || email.getText().toString().trim().isEmpty()  || cellNo.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Fill All the Fields First", Toast.LENGTH_LONG).show();
                } else {
                    DBHelper dbHelper = new DBHelper(ProfileActivity.this);
                    if (dbHelper.editUser(fullName.getText().toString().trim(), email.getText().toString().trim(), sharedPrefManager.getStringVar("password"), cellNo.getText().toString().trim(), gender)) {
                        Toast.makeText(ProfileActivity.this, "User edited successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        changePassword = (Button) findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,ChangePassword.class));
            }
        });
    }


}

