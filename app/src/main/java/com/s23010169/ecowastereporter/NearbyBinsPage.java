package com.s23010169.ecowastereporter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.s23010169.ecowastereporter.adapters.BinAdapter;
import com.s23010169.ecowastereporter.models.Bin;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NearbyBinsPage extends AppCompatActivity implements 
    BinAdapter.OnBinClickListener,
    OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private RecyclerView binsRecyclerView;
    private BinAdapter binAdapter;
    private List<Bin> allBins;
    private EditText searchEditText;
    private ExtendedFloatingActionButton filterFab;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_bins_page);

        initializeViews();
        setupRecyclerView();
        setupMap();
        setupLocationServices();
        loadDummyData();
        setupSearch();
        setupClickListeners();
    }

    private void initializeViews() {
        binsRecyclerView = findViewById(R.id.binsRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        filterFab = findViewById(R.id.filterFab);
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkLocationPermission()) {
            getLastLocation();
        } else {
            requestLocationPermission();
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void getLastLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lastKnownLocation = location;
                        updateMapLocation();
                    }
                });
        }
    }

    private void updateMapLocation() {
        if (mMap != null && lastKnownLocation != null) {
            LatLng currentLocation = new LatLng(
                lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude()
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            updateMapLocation();
        }
        updateMapMarkers();
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        mMap.clear();

        for (Bin bin : allBins) {
            float markerColor;
            if (bin.getFillPercentage() < 30) {
                markerColor = BitmapDescriptorFactory.HUE_GREEN;
            } else if (bin.getFillPercentage() < 70) {
                markerColor = BitmapDescriptorFactory.HUE_YELLOW;
            } else {
                markerColor = BitmapDescriptorFactory.HUE_RED;
            }

            mMap.addMarker(new MarkerOptions()
                .position(new LatLng(bin.getLatitude(), bin.getLongitude()))
                .title(bin.getLocation())
                .snippet("Fill Level: " + bin.getFillPercentage() + "%")
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
        }
    }

    private void setupRecyclerView() {
        binsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allBins = new ArrayList<>();
        binAdapter = new BinAdapter(this, allBins, this);
        binsRecyclerView.setAdapter(binAdapter);
    }

    private void loadDummyData() {
        List<Bin> bins = new ArrayList<>();
        bins.add(new Bin("Main Street Corner", 25, 0.2, 1.3521, 103.8198));
        bins.add(new Bin("Central Park", 75, 0.5, 1.3522, 103.8199));
        bins.add(new Bin("Shopping Mall", 50, 0.8, 1.3523, 103.8200));
        bins.add(new Bin("Bus Station", 90, 1.0, 1.3524, 103.8201));
        bins.add(new Bin("Library", 30, 1.2, 1.3525, 103.8202));
        allBins = bins;
        binAdapter.updateBins(bins);
        updateMapMarkers();
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBins(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        filterFab.setOnClickListener(v -> showFilterOptions());
    }

    private void filterBins(String query) {
        if (query.isEmpty()) {
            binAdapter.updateBins(allBins);
            updateMapMarkers();
            return;
        }

        List<Bin> filteredBins = allBins.stream()
                .filter(bin -> bin.getLocation().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        binAdapter.updateBins(filteredBins);
        
        // Update map markers with filtered bins
        mMap.clear();
        for (Bin bin : filteredBins) {
            mMap.addMarker(new MarkerOptions()
                .position(new LatLng(bin.getLatitude(), bin.getLongitude()))
                .title(bin.getLocation()));
        }
    }

    private void showFilterOptions() {
        Toast.makeText(this, "Filter options coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBinClick(Bin bin) {
        if (mMap != null) {
            LatLng binLocation = new LatLng(bin.getLatitude(), bin.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(binLocation, 17f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                if (mMap != null) {
                    if (checkLocationPermission()) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            } else {
                Toast.makeText(this, "Location permission is required to show your location",
                    Toast.LENGTH_LONG).show();
            }
        }
    }
} 