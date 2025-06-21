package com.s23010169.ecowastereporter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.s23010169.ecowastereporter.adapters.ReportAdapter;
import com.s23010169.ecowastereporter.models.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyReportPage extends AppCompatActivity {
    private RecyclerView reportsRecyclerView;
    private TabLayout tabLayout;
    private ReportAdapter reportAdapter;
    private List<Report> allReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report_page);

        // Initialize views
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        tabLayout = findViewById(R.id.tabLayout);

        // Set up RecyclerView
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize sample data
        initializeSampleData();
        
        // Set up adapter
        reportAdapter = new ReportAdapter(this, allReports);
        reportsRecyclerView.setAdapter(reportAdapter);
        
        // Set up TabLayout listener
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
    }

    private void initializeSampleData() {
        allReports = new ArrayList<>();
        allReports.add(new Report("Overflowing Bin", "Main Street Corner", "#1234", "2 days ago", "Pending"));
        allReports.add(new Report("Illegal Dumping", "Park Area", "#1231", "5 days ago", "Resolved"));
        allReports.add(new Report("Broken Bin", "Shopping Mall", "#1228", "1 week ago", "In Progress"));
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
    }
} 