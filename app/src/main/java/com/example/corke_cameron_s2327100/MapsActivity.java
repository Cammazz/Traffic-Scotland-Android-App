package com.example.corke_cameron_s2327100;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.corke_cameron_s2327100.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    // private ViewSwitcher avw;
    private ViewFlipper avw;
    private Button s1Button;
    private Button s2Button;
    private RadioGroup mapTypeGroup;
    private RadioButton normalViewButton;
    private RadioButton terrainViewButton;
    private RadioButton hybridViewButton;
    private RadioButton satelliteViewButton;
    private CheckBox panZoom;
    private Button returnButton;
    private Button filterButton;
    private Button applyFilters;
    private CheckBox filterMaintenance;
    private CheckBox filterLaneManagement;
    private CheckBox filterAccident;
    private CheckBox filterTraffic;
    private CheckBox filterObstruction;
    private CheckBox filterVehicleObs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapTypeGroup = (RadioGroup)findViewById(R.id.mapTypeGroup);
        normalViewButton = (RadioButton)findViewById(R.id.normalViewRadio);
        terrainViewButton = (RadioButton)findViewById(R.id.terrainViewRadio);
        hybridViewButton = (RadioButton)findViewById(R.id.hybridViewRadio);
        satelliteViewButton = (RadioButton)findViewById(R.id.satelliteViewRadio);
        panZoom = (CheckBox)findViewById(R.id.panZoom);
        Log.e(getPackageName(), "just before avw");
        avw = (ViewFlipper) findViewById(R.id.vwSwitch);
        if (avw == null)
        {
            Toast.makeText(getApplicationContext(), "Null ViewSwicther",
                    Toast.LENGTH_LONG).show();
            Log.e(getPackageName(), "null pointer");
        }
        filterButton = findViewById(R.id.filterButton);
        applyFilters = findViewById(R.id.applyFilters);
        filterButton.setOnClickListener(v -> { avw.setDisplayedChild(2); });
        applyFilters.setOnClickListener(v -> { displayMarkers(); avw.setDisplayedChild(0); });
        filterMaintenance = findViewById(R.id.filterMaintenance);
        filterLaneManagement = findViewById(R.id.filterLaneManagement);
        filterAccident = findViewById(R.id.filterAccident);
        filterTraffic = findViewById(R.id.filterTraffic);
        filterObstruction = findViewById(R.id.filterObstruction);
        filterVehicleObs = findViewById(R.id.filterVehicleObs);
        s1Button = (Button) findViewById(R.id.screen1Button);
        s2Button = (Button) findViewById(R.id.screen2Button);
        s1Button.setOnClickListener(this);
        s2Button.setOnClickListener(this);
        returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> { finish (); });
        normalViewButton.setOnClickListener(this);
        terrainViewButton.setOnClickListener(this);
        hybridViewButton.setOnClickListener(this);
        satelliteViewButton.setOnClickListener(this);
        normalViewButton.toggle();
        panZoom.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        Intent intent = getIntent();
//        int situationCount = intent.getIntExtra("situationCount", 0);


        LatLng glasgow = new LatLng(55.8642, -4.2518);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 11));

        displayMarkers();

//        for (int i = 0; i < situationCount; i++) {
//            double latitude = intent.getDoubleExtra("latitude_" + i, 0);
//            double longitude = intent.getDoubleExtra("longitude_" + i, 0);
//            String type = intent.getStringExtra("type_" + i);
//            String description = intent.getStringExtra("description_" + i);
//            if (latitude != 0 && longitude != 0) {
//                LatLng location = new LatLng(latitude, longitude);
//
//            mMap.addMarker(new MarkerOptions()
//                    .position(location)
//                    .title(type)
//                    .snippet(description));
//        }}
    }

    private void displayMarkers() {
        mMap.clear();
        Intent intent = getIntent();
        int situationCount = intent.getIntExtra("situationCount", 0);

        for (int i = 0; i < situationCount; i++) {
            double latitude = intent.getDoubleExtra("latitude_" + i, 0);
            double longitude = intent.getDoubleExtra("longitude_" + i, 0);
            String type = intent.getStringExtra("type_" + i);
            String description = intent.getStringExtra("description_" + i);

            boolean showMarker = false;

            if ("MaintenanceWorks".equals(type) && filterMaintenance.isChecked()) { showMarker = true; }
            else if ("RoadOrCarriagewayOrLaneManagement".equals(type) && filterLaneManagement.isChecked()) { showMarker = true; }
            else if ("Accident".equals(type) && filterAccident.isChecked()) { showMarker = true; }
            else if ("AbnormalTraffic".equals(type) && filterTraffic.isChecked()) { showMarker = true; }
            else if ("GeneralObstruction".equals(type) && filterObstruction.isChecked()) { showMarker = true; }
            else if ("VehicleObstruction".equals(type) && filterVehicleObs.isChecked()) { showMarker = true; }

            if (showMarker && latitude != 0 && longitude != 0) { LatLng location = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(type)
                        .snippet(description));
        }}
    }

    public void onClick (View arg0) {
        if (arg0 == s1Button) {
            avw.showNext(); }
        else if (arg0 == s2Button) {
            avw.showPrevious(); }
        else if (arg0 == normalViewButton) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); }
        else if (arg0 == terrainViewButton) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); }
        else if (arg0 == hybridViewButton) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); }
        else if (arg0 == satelliteViewButton) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); }
        if (panZoom.isChecked()) {
            mMap.getUiSettings().setZoomControlsEnabled(true); }
        else {
            mMap.getUiSettings().setZoomControlsEnabled(false);
        }
    }
}