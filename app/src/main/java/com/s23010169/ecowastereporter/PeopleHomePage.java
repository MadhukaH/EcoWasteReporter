package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

public class PeopleHomePage extends AppCompatActivity implements View.OnClickListener {
    private MaterialCardView reportWasteCard, viewMapCard, myReportsCard, recyclingTipsCard;
    private ExtendedFloatingActionButton reportFab;
    private NestedScrollView scrollView;
    private ShapeableImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_home_page);

        initializeViews();
        setupClickListeners();
        setupScrollBehavior();
    }

    private void initializeViews() {
        reportWasteCard = findViewById(R.id.reportWasteCard);
        viewMapCard = findViewById(R.id.viewMapCard);
        myReportsCard = findViewById(R.id.myReportsCard);
        recyclingTipsCard = findViewById(R.id.recyclingTipsCard);
        reportFab = findViewById(R.id.reportFab);
        scrollView = findViewById(R.id.scrollView);
        profileImage = findViewById(R.id.profileImage);
    }

    private void setupClickListeners() {
        reportWasteCard.setOnClickListener(this);
        viewMapCard.setOnClickListener(this);
        myReportsCard.setOnClickListener(this);
        recyclingTipsCard.setOnClickListener(this);
        reportFab.setOnClickListener(this);
        
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(PeopleHomePage.this, ProfilePage.class);
            startActivity(intent);
        });
    }

    private void setupScrollBehavior() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) 
            (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY > oldScrollY && reportFab.isExtended()) {
                    reportFab.shrink();
                }
                if (scrollY < oldScrollY && !reportFab.isExtended()) {
                    reportFab.extend();
                }
                if (scrollY == 0) {
                    reportFab.extend();
                }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.reportWasteCard || id == R.id.reportFab) {
            Intent intent = new Intent(PeopleHomePage.this, ReportWastePage.class);
            startActivity(intent);
        } else if (id == R.id.viewMapCard) {
            Intent intent = new Intent(PeopleHomePage.this, NearbyBinsPage.class);
            startActivity(intent);
        } else if (id == R.id.myReportsCard) {
            Intent intent = new Intent(PeopleHomePage.this, MyReportPage.class);
            startActivity(intent);
        } else if (id == R.id.recyclingTipsCard) {
            Intent intent = new Intent(PeopleHomePage.this, RecyclingTipsPage.class);
            startActivity(intent);
        }
    }

    private void showFeatureMessage(String feature) {
        Toast.makeText(this, feature + " feature coming soon!", Toast.LENGTH_SHORT).show();
    }
} 