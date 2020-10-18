package com.example.qasim1793.remindme.Frontend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qasim1793.remindme.Backend.DBHelper;
import com.example.qasim1793.remindme.R;
import com.example.qasim1793.remindme.Utils.SharedPrefManager;

public class ChangePassword extends AppCompatActivity {
    Button cancel,save;
    EditText password,newPassword,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        cancel = (Button) findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ChangePassword.this)
                        .setTitle("Really Cancel?")
                        .setMessage("Are you sure you want to cancel?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(ChangePassword.this,ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).create().show();

            }
        });

        save = (Button) findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(ChangePassword.this);
                if(password.getText().toString().equals(sharedPrefManager.getStringVar("password"))){
                    if(newPassword.getText().toString().equals(confirmPassword.getText().toString())){
                        DBHelper dbHelper = new DBHelper(ChangePassword.this);
                        if (dbHelper.editPassword(newPassword.getText().toString())) {
                            Toast.makeText(ChangePassword.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(ChangePassword.this,"New password And confirm password are different",Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(ChangePassword.this,"Wrong current password",Toast.LENGTH_LONG).show();
                }


            }
        });


    }
}
