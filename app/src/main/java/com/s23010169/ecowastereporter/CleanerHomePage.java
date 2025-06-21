package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CleanerHomePage extends AppCompatActivity {
    private TextView cleanerName;
    private CardView routeMapCard, markCompleteCard, viewTasksCard, performanceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_home_page);

        // Initialize views
        cleanerName = findViewById(R.id.cleanerName);
        routeMapCard = findViewById(R.id.routeMapCard);
        markCompleteCard = findViewById(R.id.markCompleteCard);
        viewTasksCard = findViewById(R.id.viewTasksCard);
        performanceCard = findViewById(R.id.performanceCard);

        // Set click listeners for cards
        routeMapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement route map functionality
            }
        });

        markCompleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement mark complete functionality
            }
        });

        viewTasksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement view tasks functionality
            }
        });

        performanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement performance functionality
            }
        });
    }
} 