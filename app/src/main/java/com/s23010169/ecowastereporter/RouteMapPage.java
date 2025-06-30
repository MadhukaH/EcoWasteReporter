package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import java.util.ArrayList;
import java.util.List;

public class RouteMapPage extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private MaterialCardView routeInfoCard;
    private TextView routeTitle, routeDistance, routeDuration, taskCount;
    private MaterialButton startNavigationButton, optimizeRouteButton;
    private FloatingActionButton myLocationButton, zoomInButton, zoomOutButton;
    private ImageView backButton;
    private List<LatLng> routePoints;
    private List<Marker> markers;
    private Polyline routePolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map_page);

        initializeViews();
        setupToolbar();
        setupMap();
        setupClickListeners();
        loadRouteData();
    }

    private void initializeViews() {
        routeInfoCard = findViewById(R.id.routeInfoCard);
        routeTitle = findViewById(R.id.routeTitle);
        routeDistance = findViewById(R.id.routeDistance);
        routeDuration = findViewById(R.id.routeDuration);
        taskCount = findViewById(R.id.taskCount);
        startNavigationButton = findViewById(R.id.startNavigationButton);
        optimizeRouteButton = findViewById(R.id.optimizeRouteButton);
        myLocationButton = findViewById(R.id.myLocationButton);
        zoomInButton = findViewById(R.id.zoomInButton);
        zoomOutButton = findViewById(R.id.zoomOutButton);
        backButton = findViewById(R.id.backButton);
        
        routePoints = new ArrayList<>();
        markers = new ArrayList<>();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());
        
        startNavigationButton.setOnClickListener(v -> {
            Toast.makeText(this, "Starting navigation...", Toast.LENGTH_SHORT).show();
            // TODO: Implement actual navigation
        });
        
        optimizeRouteButton.setOnClickListener(v -> {
            Toast.makeText(this, "Optimizing route...", Toast.LENGTH_SHORT).show();
            optimizeRoute();
        });
        
        myLocationButton.setOnClickListener(v -> {
            if (mMap != null) {
                // TODO: Get current location and move camera
                Toast.makeText(this, "Centering on your location...", Toast.LENGTH_SHORT).show();
            }
        });
        
        zoomInButton.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        
        zoomOutButton.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    private void loadRouteData() {
        // Sample route data - in a real app, this would come from a database or API
        routeTitle.setText("Today's Cleaning Route");
        routeDistance.setText("2.4 km");
        routeDuration.setText("45 min");
        taskCount.setText("7 tasks");
        
        // Sample route points (these would be actual bin locations)
        routePoints.add(new LatLng(37.7749, -122.4194)); // San Francisco
        routePoints.add(new LatLng(37.7849, -122.4094));
        routePoints.add(new LatLng(37.7949, -122.3994));
        routePoints.add(new LatLng(37.8049, -122.3894));
        routePoints.add(new LatLng(37.8149, -122.3794));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Set map properties
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        
        // Add markers for each route point
        addRouteMarkers();
        
        // Draw route line
        drawRouteLine();
        
        // Fit camera to show all markers
        fitCameraToRoute();
    }

    private void addRouteMarkers() {
        for (int i = 0; i < routePoints.size(); i++) {
            LatLng point = routePoints.get(i);
            String title = "Task " + (i + 1);
            String snippet = "Bin location " + (i + 1);
            
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(point)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            
            Marker marker = mMap.addMarker(markerOptions);
            markers.add(marker);
        }
    }

    private void drawRouteLine() {
        if (routePoints.size() > 1) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(routePoints)
                    .color(getResources().getColor(R.color.primary))
                    .width(8);
            
            routePolyline = mMap.addPolyline(polylineOptions);
        }
    }

    private void fitCameraToRoute() {
        if (routePoints.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : routePoints) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            
            int padding = 100; // padding in pixels
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }

    private void optimizeRoute() {
        // TODO: Implement route optimization algorithm
        Toast.makeText(this, "Route optimized for efficiency!", Toast.LENGTH_SHORT).show();
        
        // For demo purposes, just reverse the route
        if (routePoints.size() > 1) {
            List<LatLng> optimizedPoints = new ArrayList<>();
            for (int i = routePoints.size() - 1; i >= 0; i--) {
                optimizedPoints.add(routePoints.get(i));
            }
            routePoints = optimizedPoints;
            
            // Clear existing markers and polyline
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
            if (routePolyline != null) {
                routePolyline.remove();
            }
            
            // Redraw with optimized route
            addRouteMarkers();
            drawRouteLine();
            fitCameraToRoute();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 