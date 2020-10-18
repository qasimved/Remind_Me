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
import android.widget.Toast;

import com.example.qasim1793.remindme.Backend.LocationReminder;
import com.example.qasim1793.remindme.Backend.Reminder;
import com.example.qasim1793.remindme.Backend.ReminderDatabase;
import com.example.qasim1793.remindme.R;
import com.example.qasim1793.remindme.Utils.SharedPrefManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
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

public class AddLocationReminder extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private Location location;
    private Marker marker;
    private Button addLocationReminder;
    private EditText title,description;
    private double lat;
    private double lng;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locat);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);

        addLocationReminder = (Button) findViewById(R.id.addBtn);

        addLocationReminder.setVisibility(View.GONE);

        addLocationReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().toString().trim().isEmpty()||description.getText().toString().trim().isEmpty()){
                    Toast.makeText(AddLocationReminder.this,"Title and Description can't be empty",Toast.LENGTH_LONG).show();
                }
                else{
                    ReminderDatabase rb = new ReminderDatabase(AddLocationReminder.this);
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(AddLocationReminder.this);


                    int ID = rb.addLocationReminder(new LocationReminder(title.getText().toString(),description.getText().toString(),lat,lng,sharedPrefManager.getStringVar("_id"),"true"));

                    Toast.makeText(AddLocationReminder.this,"Alarm Added",Toast.LENGTH_LONG).show();
//                    startActivity(new Intent(AddLocationReminder.this,DashboardActivity.class));
                    finish();

                }

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

            AlertDialog.Builder dialog = new AlertDialog.Builder(AddLocationReminder.this);
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

        LatLng latLng = new LatLng(33.6519535, 73.1554936);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                String locationName = AddLocationReminder.this.getAddress(latLng.latitude, latLng.longitude);


                if (locationName == null) {
                    markerOptions.title("Unknown location");

                } else {
                    markerOptions.title(locationName);
                }

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);


                AddLocationReminder.this.addLocationReminder.setVisibility(View.VISIBLE);


                 lat = latLng.latitude;
                 lng = latLng.longitude;


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
                Geocoder geocoder = new Geocoder(AddLocationReminder.this, Locale.getDefault());
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

