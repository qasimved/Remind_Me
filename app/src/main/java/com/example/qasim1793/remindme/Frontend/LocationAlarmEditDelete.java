package com.example.qasim1793.remindme.Frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qasim1793.remindme.Backend.LocationReminder;
import com.example.qasim1793.remindme.Backend.ReminderDatabase;
import com.example.qasim1793.remindme.R;
import com.example.qasim1793.remindme.Utils.SharedPrefManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAlarmEditDelete extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private Location location;
    private MarkerOptions markerOptions;
    private EditText title,description;
    private double lat;
    private double lng;
    public static final String EXTRA_REMINDER_ID = "Reminder_ID";

    String id;
    LocationReminder locationReminder;

    private ImageView edit,delete,tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_alarm_edit_delete);

        id = getIntent().getStringExtra("id");

        ReminderDatabase reminderDatabase = new ReminderDatabase(this);

        locationReminder = reminderDatabase.getLocationReminder(id);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);

        title.setText(locationReminder.getTitle());
        description.setText(locationReminder.getDescription());
        title.setEnabled(false);
        description.setEnabled(false);

        edit = (ImageView) findViewById(R.id.edit);
        delete = (ImageView) findViewById(R.id.delete);
        tick = (ImageView) findViewById(R.id.tick);

        edit.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tick.setVisibility(View.VISIBLE);
                title.setEnabled(true);
                description.setEnabled(true);
                edit.setVisibility(View.GONE);
            }
        });

        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().toString().trim().isEmpty()||description.getText().toString().trim().isEmpty()){
                    Toast.makeText(LocationAlarmEditDelete.this,"Title and Description can't be empty",Toast.LENGTH_LONG).show();
                }
                else{
                    ReminderDatabase rb = new ReminderDatabase(LocationAlarmEditDelete.this);
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(LocationAlarmEditDelete.this);

                    rb.updateLocationReminder(new LocationReminder(Integer.valueOf(id),title.getText().toString(),description.getText().toString(),lat,lng,sharedPrefManager.getStringVar("_id"),"true"));

                    Toast.makeText(LocationAlarmEditDelete.this,"Alarm Edited",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LocationAlarmEditDelete.this,LocationReminderHistoryActivity.class));
                    finish();

                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(LocationAlarmEditDelete.this)
                        .setTitle("Really want to delete?")
                        .setMessage("Are you sure you want to delete this alarm?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

                                ReminderDatabase rb = new ReminderDatabase(LocationAlarmEditDelete.this);

                                rb.deleteLocationReminder(id);

                                Toast.makeText(LocationAlarmEditDelete.this,"Alarm Deleted",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LocationAlarmEditDelete.this,LocationReminderHistoryActivity.class));
                                finish();

                            }
                        }).create().show();


            }
        });


        if (googleServicesAvailable()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(LocationAlarmEditDelete.this);
            dialog.setTitle("Improve location accurancy?");
            dialog.setMessage("This app wants to change your device setting:");
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent, 1);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
        }





    }








    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();



        LatLng latLng = new LatLng(locationReminder.getLat(),locationReminder.getLng());

        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getAddress(locationReminder.getLat(),locationReminder.getLng()));

        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if(title.isEnabled()){
                    mMap.clear();

                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting the position for the marker
                    markerOptions.position(latLng);

                    String locationName = LocationAlarmEditDelete.this.getAddress(latLng.latitude, latLng.longitude);


                    if (locationName == null) {
                        markerOptions.title("Unknown location");

                    } else {
                        markerOptions.title(locationName);
                    }

                    // Clears the previously touched position

                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Placing a marker on the touched position
                    mMap.addMarker(markerOptions);


                    lat = latLng.latitude;
                    lng = latLng.longitude;


                }



            }
        });
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (googleApiAvailability.isUserResolvableError(isAvailable)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                } else {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                    finish();

                }
                break;
        }

    }


    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(LocationAlarmEditDelete.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getSubAdminArea();

            return add.toString();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }










}

