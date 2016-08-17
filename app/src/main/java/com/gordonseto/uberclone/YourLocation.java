package com.gordonseto.uberclone;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class YourLocation extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    TextView infoTextView;
    Button requestButton;

    private GoogleMap mMap;

    LocationManager locationManager;
    String provider;

    Boolean requestActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        infoTextView = (TextView)findViewById(R.id.infoTextView);
        requestButton = (Button)findViewById(R.id.requestButton);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (checkMapPermissions()) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    public void onRequestPressed(View view){
        if (requestActive){
            ParseQuery query = new ParseQuery<ParseObject>("Requests");
            query.whereEqualTo("requesterUsername", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        Log.i("MYAPP", "cancelling");
                        if (objects.size() > 0){
                            for (ParseObject object : objects) {
                                object.deleteInBackground();
                            }
                        }
                        rideCancelled();
                    } else {
                        Log.i("MYAPP", e.toString());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            ParseObject request = new ParseObject("Requests");
            request.put("requesterUsername", ParseUser.getCurrentUser().getUsername());
            ParseACL acl = new ParseACL();
            acl.setPublicWriteAccess(true);
            acl.setPublicReadAccess(true);
            request.setACL(acl);
            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        rideRequested();
                    } else {
                        Log.i("MYAPP", e.toString());
                    }
                }
            });
        }
    }

    public void rideCancelled(){
        requestActive = false;
        infoTextView.setText("Uber Cancelled");
        requestButton.setText("Request Uber");
    }

    public void rideRequested(){
        requestActive = true;
        infoTextView.setText("Finding Uber driver...");
        requestButton.setText("Cancel Uber");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapPermissions()) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (checkMapPermissions()) {
            locationManager.removeUpdates(this);
        }
    }

    private boolean checkMapPermissions(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null){
            onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location"));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
