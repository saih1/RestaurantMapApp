package com.saihtoo.restaurantmapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button addPlace, showMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addPlace = findViewById(R.id.addPlaceButton);
        showMap = findViewById(R.id.showMapButton);

        addPlace.setOnClickListener(this);
        showMap.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPlaceButton:
                Intent addIntent = new Intent(MainActivity.this, AddPlaceActivity.class);
                startActivity(addIntent);
                break;
            case R.id. showMapButton:
                Intent showIntent = new Intent(MainActivity.this, MapsActivity.class);
                showIntent.putExtra(AddPlaceActivity.SHOW_ON_MAP, "1010");
                startActivity(showIntent);
                break;
        }
    }
}