package com.magneticdiary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public final class GeoLocationFinder extends Activity implements LocationListener {


    private FusedLocationProviderClient mFusedLocationClient;
    private Context context;
    Location mLastLocation;
    private String latitude, longitude;
    static final int PERMISSION_ID = 44;
    static final int LOCATION_SETTINGS_REQUEST = 1;


    public GeoLocationFinder(Context context) {
        this.context = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);


    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }
        if (!requestLastLocation()) {
            ((MainActivity) context).startActivityTurnOnLocation();
        }


    }

    public boolean requestLastLocation() {
        if (!isLocationEnabled()) {
            return false;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                task -> {
                    Location location = task.getResult();
                    GeoLocationFinder.this.requestNewLocationData();

                    try {
                        assert location != null;
                        latitude = String.valueOf((mLastLocation.getLatitude()));
                        longitude = String.valueOf((mLastLocation.getLongitude()));
                        Dialog_add_place.setLatitude(latitude);
                        Dialog_add_place.setLongitude(longitude);
                        Dialog_add_place.latitudeTextView.setText(latitude);
                        Dialog_add_place.longitudeTextView.setText(longitude);
                        Dialog_add_place.removeLoader();


                    } catch (NullPointerException npe) {
                        GeoLocationFinder.this.requestLastLocation();
                    }
                }
        );
        return true;

    }

    private void startActivityTurnOnLocation() {
        Toast.makeText(context, R.string.location_permision, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ((MainActivity) context).startActivityForResult(intent, LOCATION_SETTINGS_REQUEST);
    }





    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mLastLocation = locationResult.getLastLocation();


        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                (MainActivity) context,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID

        );
    }



    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER
                );
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}