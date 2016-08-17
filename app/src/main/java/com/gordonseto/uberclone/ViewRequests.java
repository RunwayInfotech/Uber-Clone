package com.gordonseto.uberclone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewRequests extends AppCompatActivity implements LocationListener {

    ListView listView;
    ArrayList<String> content;
    ArrayList<ParseObject> requests;
    ArrayAdapter adapter;

    LocationManager locationManager;
    String provider;

    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        listView = (ListView)findViewById(R.id.listView);
        content = new ArrayList<String>();
        requests = new ArrayList<ParseObject>();

        content.add("Finding nearby requests...");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
        listView.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (checkMapPermissions()) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);

            location = locationManager.getLastKnownLocation(provider);
            if (location != null){
                onLocationChanged(location);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ViewRiderLocation.class);
                intent.putExtra("username", requests.get(i).getString("requesterUsername"));
                intent.putExtra("latitude", requests.get(i).getParseGeoPoint("requesterLocation").getLatitude());
                intent.putExtra("longitude", requests.get(i).getParseGeoPoint("requesterLocation").getLongitude());
                intent.putExtra("userLatitude", location.getLatitude());
                intent.putExtra("userLongitude", location.getLongitude());
                startActivity(intent);
            }
        });
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
    public void onLocationChanged(Location location) {
        final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
        query.whereNear("requesterLocation", userLocation);
        query.whereDoesNotExist("driverUsername");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (content != null) {content.clear();}
                    if (requests != null) {requests.clear();}
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            content.add(String.format("%.1g km", (double)Math.round(userLocation.distanceInKilometersTo(object.getParseGeoPoint("requesterLocation"))*10)/10));
                            requests.add(object);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
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
