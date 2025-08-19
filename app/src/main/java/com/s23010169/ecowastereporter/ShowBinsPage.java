package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.s23010169.ecowastereporter.adapters.BinAdapter;
import com.s23010169.ecowastereporter.models.Bin;
import com.s23010169.ecowastereporter.models.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShowBinsPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BinAdapter adapter;
    private List<Bin> allBins;
    private EditText searchEditText;
    private ExtendedFloatingActionButton filterFab;
    private BottomSheetDialog filterBottomSheet;
    private String currentFillLevelFilter = "all";
    private float minDistance = 0f;
    private float maxDistance = 5f;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bins_page);

        recyclerView = findViewById(R.id.recyclerViewShowBins);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        searchEditText = findViewById(R.id.searchEditText);
        filterFab = findViewById(R.id.filterFab);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);
        
        // Load bins from database
        loadBinsFromDatabase();

        adapter = new BinAdapter(this, new ArrayList<>(allBins), bin -> {
            // Navigate to RouteMapPage with bin location
            Intent intent = new Intent(ShowBinsPage.this, RouteMapPage.class);
            intent.putExtra("latitude", bin.getLatitude());
            intent.putExtra("longitude", bin.getLongitude());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        setupSearch();
        setupFilterBottomSheet();
        setupClickListeners();
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
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_filter, null);
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

        adapter.updateBins(filteredBins);
    }

    private void setupClickListeners() {
        filterFab.setOnClickListener(v -> filterBottomSheet.show());
    }

    private void filterBins(String query) {
        if (query.isEmpty()) {
            adapter.updateBins(new ArrayList<>(allBins));
            return;
        }

        List<Bin> filteredBins = allBins.stream()
                .filter(bin -> bin.getLocation().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        adapter.updateBins(filteredBins);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBinsFromDatabase();
        adapter.updateBins(new ArrayList<>(allBins));
    }

    private void loadBinsFromDatabase() {
        try {
            allBins = databaseHelper.getAllBins();
            if (allBins == null || allBins.isEmpty()) {
                // If no bins in database, add sample data
                databaseHelper.addSampleBins();
                allBins = databaseHelper.getAllBins();
                if (allBins == null) {
                    allBins = new ArrayList<>();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            allBins = new ArrayList<>();
            Toast.makeText(this, "Error loading bins: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 