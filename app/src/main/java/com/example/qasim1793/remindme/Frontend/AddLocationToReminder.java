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
import android.util.Log;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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

public class AddLocationToReminder extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private Location location;
    private Marker marker;
    private Button addLocationReminder;
    private double lat;
    private double lng;
    private String locationTitle;
    boolean checkForLocation = false;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private LatLng currentLatlng;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_to_reminder);

        Intent intent = getIntent();
        if(intent.getStringExtra("lat")!=null && intent.getStringExtra("lng")!=null && !intent.getStringExtra("lat").equals("") && !intent.getStringExtra("lng").equals("")){
            lat =Double.parseDouble(intent.getStringExtra("lat"));
            lng =Double.parseDouble(intent.getStringExtra("lng"));
            checkForLocation = true;
        }


        addLocationReminder = (Button) findViewById(R.id.addBtn);

        addLocationReminder.setVisibility(View.GONE);

        addLocationReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("lat",String.valueOf(lat));
                intent.putExtra("lng",String.valueOf(lng));
                intent.putExtra("locationTitle",locationTitle);
                setResult(1,intent);
                finish();
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

            AlertDialog.Builder dialog = new AlertDialog.Builder(AddLocationToReminder.this);
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


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(AddLocationToReminder.class.getName(), "Place: " + place.getName());
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(place.getLatLng());

                String locationName = AddLocationToReminder.this.getAddress(place.getLatLng().latitude, place.getLatLng().longitude);


                if (locationName == null) {
                    markerOptions.title("Unknown location");

                } else {
                    markerOptions.title(locationName);
                }

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);


                AddLocationToReminder.this.addLocationReminder.setVisibility(View.VISIBLE);


                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;

                locationTitle = place.getAddress().toString();



            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
            }
        });


//        try {
//            Intent intent =
//                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                            .build(this);
//            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//        } catch (GooglePlayServicesRepairableException e) {
//            // TODO: Handle the error.
//        } catch (GooglePlayServicesNotAvailableException e) {
//            // TODO: Handle the error.
//        }


        ImageView currentLocation= (ImageView) findViewById(R.id.currentLocationBtn);
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                placeCuttentmarker();
            }
        });


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
        this.location = location;

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


        if(checkForLocation){
            MarkerOptions markerOptions = new MarkerOptions();

            // Setting the position for the marker



            String locationName = AddLocationToReminder.this.getAddress(lat, lng);

            LatLng latLng = new LatLng(lat,lng);


            markerOptions.position(latLng);

            if (locationName == null) {
                markerOptions.title("Unknown location");
                locationTitle = "Unknown location";

            } else {
                markerOptions.title(locationName);
                locationTitle = locationName;
            }

            // Clears the previously touched position
            mMap.clear();

            // Animating to the touched position
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Placing a marker on the touched position
            mMap.addMarker(markerOptions);


            AddLocationToReminder.this.addLocationReminder.setVisibility(View.VISIBLE);


        }
        else{
            placeCuttentmarker();
        }


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                String locationName = AddLocationToReminder.this.getAddress(latLng.latitude, latLng.longitude);


                if (locationName == null) {
                    markerOptions.title("Unknown location");
                    locationTitle = "Unknown location";

                } else {
                    markerOptions.title(locationName);
                    locationTitle = locationName;
                }

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);


                AddLocationToReminder.this.addLocationReminder.setVisibility(View.VISIBLE);


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
        Geocoder geocoder = new Geocoder(AddLocationToReminder.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getSubAdminArea();

            return add.toString();


        } catch (Exception e) {
            // TODO Auto-generated catch block

        }

        return null;
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlaceAutocomplete.getPlace(this, data);
//                MarkerOptions markerOptions = new MarkerOptions();
//
//                // Setting the position for the marker
//                markerOptions.position(place.getLatLng());
//
//                String locationName = AddLocationToReminder.this.getAddress(place.getLatLng().latitude, place.getLatLng().longitude);
//
//
//                if (locationName == null) {
//                    markerOptions.title("Unknown location");
//
//                } else {
//                    markerOptions.title(locationName);
//                }
//
//                // Clears the previously touched position
//                mMap.clear();
//
//                // Animating to the touched position
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
//
//                // Placing a marker on the touched position
//                mMap.addMarker(markerOptions);
//
//
//                AddLocationToReminder.this.addLocationReminder.setVisibility(View.VISIBLE);
//
//
//                lat = place.getLatLng().latitude;
//                lng = place.getLatLng().longitude;
//
//
//
//
//
//            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//                // TODO: Handle the error.
//
//
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
//    }


    public void placeCuttentmarker(){
        try {
            if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.addMarker(markerOptions);
                markerOptions.title("You are here");
                currentLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
        catch (Exception e){
            
        }

    }



}
