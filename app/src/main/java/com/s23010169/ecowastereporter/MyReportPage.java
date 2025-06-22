package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.s23010169.ecowastereporter.adapters.ReportAdapter;
import com.s23010169.ecowastereporter.models.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MyReportPage extends AppCompatActivity {
    private RecyclerView reportsRecyclerView;
    private TabLayout tabLayout;
    private ReportAdapter reportAdapter;
    private List<Report> allReports;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyStateLayout;
    private ProgressBar loadingProgressBar;
    private MaterialCardView searchCard;
    private EditText searchEditText;
    private ImageButton searchButton;
    private ExtendedFloatingActionButton newReportFab;
    private boolean isSearchVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report_page);

        initializeViews();
        setupRecyclerView();
        initializeSampleData();
        setupListeners();
        updateEmptyState();
    }

    private void initializeViews() {
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        searchCard = findViewById(R.id.searchCard);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        newReportFab = findViewById(R.id.newReportFab);
    }

    private void setupRecyclerView() {
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this, new ArrayList<>());
        reportsRecyclerView.setAdapter(reportAdapter);
    }

    private void setupListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterReports(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        swipeRefreshLayout.setOnRefreshListener(this::refreshReports);

        searchButton.setOnClickListener(v -> toggleSearch());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterReportsBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        newReportFab.setOnClickListener(v -> {
            Intent intent = new Intent(MyReportPage.this, ReportWastePage.class);
            startActivity(intent);
        });

        // Hide/Show FAB on scroll
        reportsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    newReportFab.shrink();
                else if (dy < 0)
                    newReportFab.extend();
            }
        });
    }

    private void toggleSearch() {
        isSearchVisible = !isSearchVisible;
        searchCard.setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);
        if (!isSearchVisible) {
            searchEditText.setText("");
            filterReports(tabLayout.getSelectedTabPosition());
        }
    }

    private void filterReportsBySearch(String query) {
        if (query.isEmpty()) {
            filterReports(tabLayout.getSelectedTabPosition());
            return;
        }

        List<Report> searchResults = allReports.stream()
            .filter(report -> 
                report.getWasteType().toLowerCase().contains(query.toLowerCase()) ||
                report.getLocation().toLowerCase().contains(query.toLowerCase()) ||
                report.getReportId().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());

        reportAdapter.updateReports(searchResults);
        updateEmptyState();
    }

    private void refreshReports() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        reportsRecyclerView.setVisibility(View.GONE);

        // Simulate network delay
        reportsRecyclerView.postDelayed(() -> {
            initializeSampleData();
            filterReports(tabLayout.getSelectedTabPosition());
            loadingProgressBar.setVisibility(View.GONE);
            reportsRecyclerView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            updateEmptyState();
        }, 1000);
    }

    private void initializeSampleData() {
        allReports = new ArrayList<>();
        
        // Create sample reports with the new Report model structure
        Report report1 = new Report();
        report1.setWasteType("Overflowing Bin");
        report1.setLocation("Main Street Corner");
        report1.setReportId(UUID.randomUUID().toString().substring(0, 4));
        report1.setTimestamp(System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000)); // 2 days ago
        report1.setStatus("Pending");
        
        Report report2 = new Report();
        report2.setWasteType("Illegal Dumping");
        report2.setLocation("Park Area");
        report2.setReportId(UUID.randomUUID().toString().substring(0, 4));
        report2.setTimestamp(System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000)); // 5 days ago
        report2.setStatus("Resolved");
        
        Report report3 = new Report();
        report3.setWasteType("Broken Bin");
        report3.setLocation("Shopping Mall");
        report3.setReportId(UUID.randomUUID().toString().substring(0, 4));
        report3.setTimestamp(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)); // 1 week ago
        report3.setStatus("In Progress");
        
        allReports.add(report1);
        allReports.add(report2);
        allReports.add(report3);
    }

    private void filterReports(int tabPosition) {
        List<Report> filteredReports;
        switch (tabPosition) {
            case 1: // Pending
                filteredReports = allReports.stream()
                    .filter(report -> report.getStatus().equalsIgnoreCase("Pending"))
                    .collect(Collectors.toList());
                break;
            case 2: // Resolved
                filteredReports = allReports.stream()
                    .filter(report -> report.getStatus().equalsIgnoreCase("Resolved"))
                    .collect(Collectors.toList());
                break;
            default: // All
                filteredReports = new ArrayList<>(allReports);
                break;
        }
        reportAdapter.updateReports(filteredReports);
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = reportAdapter.getItemCount() == 0;
        emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        reportsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
} 