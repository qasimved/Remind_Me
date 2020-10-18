package com.example.qasim1793.remindme.Frontend;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qasim1793.remindme.Backend.BackgroundService;
import com.example.qasim1793.remindme.Backend.ReminderDatabase;
import com.example.qasim1793.remindme.BuildConfig;
import com.example.qasim1793.remindme.R;
import com.example.qasim1793.remindme.Utils.SharedPrefManager;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.function.ToLongBiFunction;

public class DashboardActivity extends AppCompatActivity {
//    public LinearLayout block1,block2,block3,block4;

    public CardView option1,option2,option3,option4,option5,option6,option7,option8;
    public ImageView logout;
    public TextView name;
    public SharedPrefManager sharedPrefManager;
    public BackgroundService gpsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_redesign_2);

       sharedPrefManager = SharedPrefManager.getInstance(this);
        name = (TextView) findViewById(R.id.name);
        name.setText(sharedPrefManager.getStringVar("fullName"));

        ReminderDatabase DB = new ReminderDatabase(getApplicationContext());

        option1 = (CardView) findViewById(R.id.block_1);
        option2 = (CardView) findViewById(R.id.block_2);
        option3 = (CardView) findViewById(R.id.block_3);
        option4 = (CardView) findViewById(R.id.block_4);
        option5 = (CardView) findViewById(R.id.block_5);
        option6 = (CardView) findViewById(R.id.block_6);
        option7 = (CardView) findViewById(R.id.block_7);
        option8 = (CardView) findViewById(R.id.block_8);
        logout = (ImageView) findViewById(R.id.logout);


        try {
            this.startService(new Intent(this, LocationServices.class));
        }
        catch (Exception e){

        }




        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        final Intent intent = new Intent(DashboardActivity.this.getApplication(), BackgroundService.class);
                        DashboardActivity.this.getApplication().startService(intent);
                        DashboardActivity.this.getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//                        gpsService.startTracking();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();



//        gpsService.startTracking();


        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,ReminderAddActivity.class));
            }
        });
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,AddLocationReminder.class));
            }
        });
        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,ReminderHistoryActivity.class));
            }
        });
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,ProfileActivity.class));
            }
        });
        option5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,AddNewCategoryActivity.class));

            }
        });
        option6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,ReminderActivity.class));
            }
        });
        option7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,LocationReminderHistoryActivity.class));
            }
        });
        option8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,FeedbackActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DashboardActivity.this)
                        .setTitle("Really Logout?")
                        .setMessage("Are you sure you want to logout?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                DashboardActivity.this.sharedPrefManager.logout();
                                startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
                                finish();
                            }
                        }).create().show();
            }
        });



//        block1 = (LinearLayout) findViewById(R.id.block1);
//        block2 = (LinearLayout) findViewById(R.id.block2);
//        block3 = (LinearLayout) findViewById(R.id.block3);
//        block4 = (LinearLayout) findViewById(R.id.block4);
//
//
//        block1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        block2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DashboardActivity.this,ReminderActivity.class));
//            }
//        });
//
//        block3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DashboardActivity.this,TrackingActivity.class));
//
//            }
//        });
//
//        block4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                new AlertDialog.Builder(DashboardActivity.this)
//                        .setTitle("Really Logout?")
//                        .setMessage("Are you sure you want to logout?")
//                        .setNegativeButton(android.R.string.no, null)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface arg0, int arg1) {
//                                startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
//                                finish();
//                            }
//                        }).create().show();
//
//
//
//            }
//        });
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(DashboardActivity.this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit application?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        name.setText(sharedPrefManager.getStringVar("fullName"));
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("BackgroundService")) {
                gpsService = ((BackgroundService.LocationServiceBinder) service).getService();

            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };

    @Override
    protected void onDestroy() {
        gpsService.stopTracking();
        super.onDestroy();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
