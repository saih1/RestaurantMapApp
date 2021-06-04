package com.saihtoo.restaurantmapapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.saihtoo.restaurantmapapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    DBHelper db;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();

        if (intent.getStringExtra(AddPlaceActivity.SHOW_ON_MAP).equals("1001")) {
            String locationName = intent.getStringExtra(AddPlaceActivity.NAME);
            double locationLat = intent.getDoubleExtra(AddPlaceActivity.LAT, 0);
            double locationLng = intent.getDoubleExtra(AddPlaceActivity.LNG, 0);

            LatLng mLocation = new LatLng(locationLat, locationLng);
            mMap.addMarker(new MarkerOptions().position(mLocation).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 18));

        } else if (intent.getStringExtra(AddPlaceActivity.SHOW_ON_MAP).equals("1010")) {

            db = new DBHelper(this);
            Cursor cursor = db.getAllPlaces();

            while (cursor.moveToNext()) {
                String name = cursor.getString(2);
                double lat = cursor.getDouble(3);
                double lng = cursor.getDouble(4);
                LatLng myLocation = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(myLocation).title(name));
            }

            //developers.google.com/maps/documentation/android-sdk/views
            LatLngBounds australiaBounds = new LatLngBounds(
                    new LatLng(-44, 113), // SW bounds
                    new LatLng(-10, 154)  // NE bounds
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(australiaBounds.getCenter(), 4));
        }

    }
}