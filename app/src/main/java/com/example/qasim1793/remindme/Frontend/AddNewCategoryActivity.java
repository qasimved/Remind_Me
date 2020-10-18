package com.example.qasim1793.remindme.Frontend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qasim1793.remindme.Backend.DBHelper;
import com.example.qasim1793.remindme.R;

public class AddNewCategoryActivity extends AppCompatActivity {
    Button cancel,save;
    EditText type,name,details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);

        type = (EditText) findViewById(R.id.type);
        name = (EditText) findViewById(R.id.name);
        details = (EditText) findViewById(R.id.details);

        cancel = (Button) findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AddNewCategoryActivity.this)
                        .setTitle("Really Cancel?")
                        .setMessage("Are you sure you want to cancel?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
//                                Intent intent = new Intent(AddNewCategoryActivity.this,DashboardActivity.class);
//                                startActivity(intent);
                                finish();
                            }
                        }).create().show();

            }
        });

        save = (Button) findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(AddNewCategoryActivity.this);
                if(dbHelper.addCategory(type.getText().toString(),name.getText().toString(),details.getText().toString())){
//                    Intent intent = new Intent(AddNewCategoryActivity.this,DashboardActivity.class);
//                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(AddNewCategoryActivity.this,"Category Already Exist",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
