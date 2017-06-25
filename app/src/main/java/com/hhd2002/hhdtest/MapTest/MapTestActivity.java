package com.hhd2002.hhdtest.MapTest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hhd2002.hhdtest.R;

public class MapTestActivity extends FragmentActivity {
    private GoogleMap _map; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map_test);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (_map == null) {
            SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            smf.getMapAsync(googleMap -> {
                _map = googleMap;
                _SetSydney();
            });
        } else {
            _SetSydney();
        }
    }

    private void _SetSydney() {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        _map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        _map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}