package com.example.qasim1793.remindme.Frontend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qasim1793.remindme.R;
import com.example.qasim1793.remindme.Utils.NetUtils;

public class FeedbackActivity extends AppCompatActivity {
    Button cancel,save;
    TextView name,email,mobile,feedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        mobile = (TextView) findViewById(R.id.mobile);
        feedback = (TextView) findViewById(R.id.feedback);

        cancel = (Button) findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(FeedbackActivity.this)
                        .setTitle("Really Cancel?")
                        .setMessage("Are you sure you want to cancel?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

//                                Intent intent = new Intent(FeedbackActivity.this,DashboardActivity.class);
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
                if(NetUtils.isNetConnected(FeedbackActivity.this)) {
                    String body = "Name : " + name.getText() + "\nEmail : " + email.getText() + "\nMobile No. : " + mobile.getText() + "\nFeedback : " + feedback.getText();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"qasim.jeved345@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "FeedBack");
                    i.putExtra(Intent.EXTRA_TEXT, body);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FeedbackActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(FeedbackActivity.this, "There is no internet to send email, Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        Intent intent = new Intent(FeedbackActivity.this, DashboardActivity.class);
//        startActivity(intent);
        finish();
    }
}
