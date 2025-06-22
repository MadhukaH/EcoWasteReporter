package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.s23010169.ecowastereporter.adapters.ActionAdapter;
import com.s23010169.ecowastereporter.models.Action;

import java.util.ArrayList;
import java.util.List;

public class CleanerHomePage extends AppCompatActivity implements ActionAdapter.ActionClickListener {
    private TextView cleanerName;
    private MaterialCardView tasksCard, completedCard;
    private RecyclerView actionsRecyclerView;
    private LinearProgressIndicator taskProgress;
    private ExtendedFloatingActionButton startRouteButton;
    private ImageView notificationIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_home_page);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        updateTaskProgress();
    }

    private void initializeViews() {
        cleanerName = findViewById(R.id.cleanerName);
        tasksCard = findViewById(R.id.tasksCard);
        completedCard = findViewById(R.id.completedCard);
        actionsRecyclerView = findViewById(R.id.actionsRecyclerView);
        taskProgress = findViewById(R.id.taskProgress);
        startRouteButton = findViewById(R.id.startRouteButton);
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupRecyclerView() {
        List<Action> actions = new ArrayList<>();
        actions.add(new Action(R.drawable.ic_route_map, "Route Map", () -> handleActionClick("Route Map")));
        actions.add(new Action(R.drawable.ic_mark_complete, "Mark Complete", () -> handleActionClick("Mark Complete")));
        actions.add(new Action(R.drawable.ic_view_tasks, "View Tasks", () -> handleActionClick("View Tasks")));
        actions.add(new Action(R.drawable.ic_performance, "Performance", () -> handleActionClick("Performance")));

        ActionAdapter adapter = new ActionAdapter(this, actions, this);
        actionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        actionsRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        tasksCard.setOnClickListener(v -> showTasksDetail());
        completedCard.setOnClickListener(v -> showCompletedTasksDetail());
        startRouteButton.setOnClickListener(v -> startRoute());
        notificationIcon.setOnClickListener(v -> showNotifications());
        profileIcon.setOnClickListener(v -> navigateToProfile());
    }

    private void updateTaskProgress() {
        taskProgress.setProgress(42); // This would be dynamic in a real app
    }

    @Override
    public void onActionClick(Action action) {
        handleActionClick(action.getTitle());
    }

    private void handleActionClick(String actionTitle) {
        switch (actionTitle) {
            case "Route Map":
                showRouteMap();
                break;
            case "Mark Complete":
                showMarkComplete();
                break;
            case "View Tasks":
                showTasksList();
                break;
            case "Performance":
                showPerformanceStats();
                break;
        }
    }

    private void showTasksDetail() {
        showToast("Showing detailed tasks breakdown...");
        // TODO: Implement tasks detail view
    }

    private void showCompletedTasksDetail() {
        showToast("Showing completed tasks history...");
        // TODO: Implement completed tasks history
    }

    private void startRoute() {
        showToast("Starting route navigation...");
        startRouteButton.extend(); // Show full FAB text
        // TODO: Implement route navigation
    }

    private void showNotifications() {
        showToast("Opening notifications...");
        // TODO: Implement notifications view
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }

    private void showRouteMap() {
        showToast("Opening route map...");
        // TODO: Implement route map view
    }

    private void showMarkComplete() {
        showToast("Opening task completion form...");
        // TODO: Implement task completion
    }

    private void showTasksList() {
        showToast("Opening tasks list...");
        Intent intent = new Intent(this, ViewTasksPage.class);
        startActivity(intent);
    }

    private void showPerformanceStats() {
        showToast("Opening performance statistics...");
        // TODO: Implement performance stats view
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
} 