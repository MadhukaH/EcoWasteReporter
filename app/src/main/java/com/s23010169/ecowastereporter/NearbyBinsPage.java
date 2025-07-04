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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.RangeSlider;
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
    private BottomSheetDialog filterBottomSheet;
    private String currentFillLevelFilter = "all";
    private float minDistance = 0f;
    private float maxDistance = 5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_bins_page);

        initializeViews();
        setupRecyclerView();
        setupMap();
        setupLocationServices();
        setupFilterBottomSheet();
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
        // Matara City and nearby locations
        bins.add(new Bin("Matara Bus Stand Recycling Point", 45, 0.2, 5.9549, 80.5550));
        bins.add(new Bin("Uyanwatta Stadium Waste Bin", 30, 0.5, 5.9539, 80.5535));
        bins.add(new Bin("Matara Beach Recycling Center", 75, 0.8, 5.9485, 80.5453));
        bins.add(new Bin("Star Fort Waste Collection", 25, 1.0, 5.9527, 80.5477));
        bins.add(new Bin("Matara Central Market Bin", 90, 0.7, 5.9520, 80.5424));
        bins.add(new Bin("Nupe Junction Recycling Point", 60, 1.2, 5.9572, 80.5506));
        bins.add(new Bin("Rahula College Area Bin", 40, 0.9, 5.9563, 80.5559));
        bins.add(new Bin("Matara Railway Station Bin", 55, 0.6, 5.9515, 80.5443));
        bins.add(new Bin("Sanath Jayasuriya Ground Bin", 35, 1.1, 5.9533, 80.5518));
        bins.add(new Bin("Matara Hospital Waste Point", 85, 0.4, 5.9508, 80.5435));
        allBins = bins;
        binAdapter.updateBins(bins);
        updateMapMarkers();
        
        // Set initial map focus to Matara city center
        if (mMap != null) {
            LatLng mataraCityCenter = new LatLng(5.9549, 80.5550);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mataraCityCenter, 14f));
        }
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

    private void setupFilterBottomSheet() {
        filterBottomSheet = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        filterBottomSheet.setContentView(bottomSheetView);

        ChipGroup fillLevelChipGroup = bottomSheetView.findViewById(R.id.fillLevelChipGroup);
        RangeSlider distanceSlider = bottomSheetView.findViewById(R.id.distanceSlider);
        MaterialButton applyButton = bottomSheetView.findViewById(R.id.applyFilterButton);
        MaterialButton resetButton = bottomSheetView.findViewById(R.id.resetFilterButton);

        fillLevelChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAll) {
                currentFillLevelFilter = "all";
            } else if (checkedId == R.id.chipEmpty) {
                currentFillLevelFilter = "empty";
            } else if (checkedId == R.id.chipHalfFull) {
                currentFillLevelFilter = "half";
            } else if (checkedId == R.id.chipFull) {
                currentFillLevelFilter = "full";
            }
        });

        distanceSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            minDistance = values.get(0);
            maxDistance = values.get(1);
        });

        applyButton.setOnClickListener(v -> {
            applyFilters();
            filterBottomSheet.dismiss();
        });

        resetButton.setOnClickListener(v -> {
            resetFilters(fillLevelChipGroup, distanceSlider);
        });
    }

    private void resetFilters(ChipGroup fillLevelChipGroup, RangeSlider distanceSlider) {
        currentFillLevelFilter = "all";
        minDistance = 0f;
        maxDistance = 5f;
        
        fillLevelChipGroup.check(R.id.chipAll);
        distanceSlider.setValues(0f, 5f);
        
        applyFilters();
    }

    private void applyFilters() {
        List<Bin> filteredBins = allBins.stream()
            .filter(bin -> {
                boolean matchesFillLevel = true;
                boolean matchesDistance = bin.getDistance() >= minDistance && bin.getDistance() <= maxDistance;

                switch (currentFillLevelFilter) {
                    case "empty":
                        matchesFillLevel = bin.getFillPercentage() < 30;
                        break;
                    case "half":
                        matchesFillLevel = bin.getFillPercentage() >= 30 && bin.getFillPercentage() <= 70;
                        break;
                    case "full":
                        matchesFillLevel = bin.getFillPercentage() > 70;
                        break;
                }

                return matchesFillLevel && matchesDistance;
            })
            .collect(Collectors.toList());

        binAdapter.updateBins(filteredBins);
        updateMapWithBins(filteredBins);
    }

    private void updateMapWithBins(List<Bin> bins) {
        if (mMap == null) return;
        mMap.clear();

        for (Bin bin : bins) {
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

    private void setupClickListeners() {
        filterFab.setOnClickListener(v -> filterBottomSheet.show());
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