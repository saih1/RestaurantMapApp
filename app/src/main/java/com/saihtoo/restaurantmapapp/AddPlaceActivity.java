package com.saihtoo.restaurantmapapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.UUID;

public class AddPlaceActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddPlaceActivity";
    public static final String NAME = "selected_name";
    public static final String LAT = "selected_lat";
    public static final String LNG = "selected_lng";
    public static final String SHOW_ON_MAP = "show_on_map";

    Button currentLocationBtn, showOnMapBtn, saveBtn;
    EditText placeNameText;

    AutocompleteSupportFragment autocompleteFragment;

    String selectedID;
    String selectedName;
    double selectedLat, selectedLng;

    LocationManager locationManager;
    LocationListener locationListener;

    Boolean trackingStatus = false;

    DBHelper db;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        placeNameText = findViewById(R.id.placeNameEditText);
        currentLocationBtn = findViewById(R.id.currentLocationButton);
        showOnMapBtn = findViewById(R.id.showOnMapButton);
        saveBtn = findViewById(R.id.saveButton);

        currentLocationBtn.setOnClickListener(this);
        showOnMapBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.Places_API_Key));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                selectedID = place.getId();
                selectedLat = place.getLatLng().latitude;
                selectedLng = place.getLatLng().longitude;
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.currentLocationButton:
                locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        //finding user's location and stopping when found
                        if (!trackingStatus) {
                            selectedLat = location.getLatitude();
                            selectedLng = location.getLongitude();
                            selectedID = UUID.randomUUID().toString();
                            autocompleteFragment.setText("lat/lng: (" + selectedLat + "," + selectedLng +")");
                            Toast.makeText(AddPlaceActivity.this, "Finding your location ... ", Toast.LENGTH_LONG).show();
                            trackingStatus = true;
                        } else {
                            locationManager.removeUpdates(this);
                            Toast.makeText(AddPlaceActivity.this, "Your location found! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                break;

            case R.id.showOnMapButton:
                selectedName = placeNameText.getText().toString();
                Intent showIntent = new Intent(AddPlaceActivity.this, MapsActivity.class);
                showIntent.putExtra(LAT, selectedLat);
                showIntent.putExtra(LNG, selectedLng);
                showIntent.putExtra(NAME, selectedName);
                showIntent.putExtra(SHOW_ON_MAP, "1001");
                startActivity(showIntent);
                break;

            case R.id.saveButton:
                selectedName = placeNameText.getText().toString();
                if (!selectedName.isEmpty() && selectedID != null) {
                    db = new DBHelper(this);
                    long newRowID = db.addLocation(selectedName, selectedID, selectedLat, selectedLng);
                    Toast.makeText(AddPlaceActivity.this,
                            selectedName + " has been added", Toast.LENGTH_SHORT).show();
                    Intent backIntent = new Intent(AddPlaceActivity.this, MainActivity.class);
                    startActivity(backIntent);
                } break;
        }
    }
}