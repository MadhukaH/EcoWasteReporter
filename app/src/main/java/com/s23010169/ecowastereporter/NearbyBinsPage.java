package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.s23010169.ecowastereporter.adapters.BinAdapter;
import com.s23010169.ecowastereporter.models.Bin;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NearbyBinsPage extends AppCompatActivity implements BinAdapter.OnBinClickListener {
    private RecyclerView binsRecyclerView;
    private BinAdapter binAdapter;
    private List<Bin> allBins;
    private EditText searchEditText;
    private ExtendedFloatingActionButton filterFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_bins_page);

        initializeViews();
        setupRecyclerView();
        loadDummyData();
        setupSearch();
        setupClickListeners();
    }

    private void initializeViews() {
        binsRecyclerView = findViewById(R.id.binsRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        filterFab = findViewById(R.id.filterFab);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.mapCard).setOnClickListener(v -> showMapView());
    }

    private void setupRecyclerView() {
        binsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allBins = new ArrayList<>();
        binAdapter = new BinAdapter(this, allBins, this);
        binsRecyclerView.setAdapter(binAdapter);
    }

    private void loadDummyData() {
        // This would be replaced with real data from an API or database
        List<Bin> bins = new ArrayList<>();
        bins.add(new Bin("Main Street Corner", 25, 0.2, 1.3521, 103.8198));
        bins.add(new Bin("Central Park", 75, 0.5, 1.3522, 103.8199));
        bins.add(new Bin("Shopping Mall", 50, 0.8, 1.3523, 103.8200));
        bins.add(new Bin("Bus Station", 90, 1.0, 1.3524, 103.8201));
        bins.add(new Bin("Library", 30, 1.2, 1.3525, 103.8202));
        allBins = bins;
        binAdapter.updateBins(bins);
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
            return;
        }

        List<Bin> filteredBins = allBins.stream()
                .filter(bin -> bin.getLocation().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        binAdapter.updateBins(filteredBins);
    }

    private void showFilterOptions() {
        // This would show a bottom sheet with filter options
        Toast.makeText(this, "Filter options coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void showMapView() {
        // This would launch the map view
        Toast.makeText(this, "Map view coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBinClick(Bin bin) {
        // This would show detailed information about the bin
        String message = String.format("Selected bin at %s (%.1f km away)", 
            bin.getLocation(), bin.getDistance());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
} 