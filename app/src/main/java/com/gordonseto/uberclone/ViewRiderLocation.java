package com.gordonseto.uberclone;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewRiderLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Intent intent;
    RelativeLayout mapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rider_location);

        mapLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        intent = getIntent();
        Log.i("MYAPP", intent.getStringExtra("username") + " " + intent.getDoubleExtra("latitude", 0) + " " + intent.getDoubleExtra("longitude", 0));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ArrayList<Marker> markers = new ArrayList<Marker>();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0)), 15));
                markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0))).title("Rider Location")));
                markers.add(mMap.addMarker(new MarkerOptions(). position(new LatLng(intent.getDoubleExtra("userLatitude", 0), intent.getDoubleExtra("userLongitude", 0))).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers){
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                int padding = 100;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }
        });
    }

    public void onAcceptPressed(View view){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
        query.whereEqualTo("requesterUsername", intent.getStringExtra("username"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0){
                        for (ParseObject object : objects){
                            object.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground();
                        }
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + intent.getDoubleExtra("userLatitude", 0) + "," + intent.getDoubleExtra("userLongitude", 0)));
                    startActivity(i);
                }
            }
        });
    }

    public void onBackPressed(View view){
        finish();
    }
}
