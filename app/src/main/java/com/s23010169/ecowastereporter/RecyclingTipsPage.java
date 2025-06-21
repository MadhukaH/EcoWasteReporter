package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class RecyclingTipsPage extends AppCompatActivity {
    private Button btnAllTips, btnPlastic, btnPaper;
    private CardView plasticCard, paperCard, metalCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_tips_page);

        // Initialize views
        btnAllTips = findViewById(R.id.btnAllTips);
        btnPlastic = findViewById(R.id.btnPlastic);
        btnPaper = findViewById(R.id.btnPaper);

        // Set initial state
        btnAllTips.setSelected(true);
        btnPlastic.setSelected(false);
        btnPaper.setSelected(false);

        // Set click listeners for tabs
        btnAllTips.setOnClickListener(v -> {
            setTabSelected(btnAllTips);
            showAllCards();
        });

        btnPlastic.setOnClickListener(v -> {
            setTabSelected(btnPlastic);
            showPlasticOnly();
        });

        btnPaper.setOnClickListener(v -> {
            setTabSelected(btnPaper);
            showPaperOnly();
        });
    }

    private void setTabSelected(Button selectedButton) {
        btnAllTips.setSelected(selectedButton == btnAllTips);
        btnPlastic.setSelected(selectedButton == btnPlastic);
        btnPaper.setSelected(selectedButton == btnPaper);
    }

    private void showAllCards() {
        // Show all cards
        if (plasticCard != null) plasticCard.setVisibility(View.VISIBLE);
        if (paperCard != null) paperCard.setVisibility(View.VISIBLE);
        if (metalCard != null) metalCard.setVisibility(View.VISIBLE);
    }

    private void showPlasticOnly() {
        // Show only plastic card
        if (plasticCard != null) plasticCard.setVisibility(View.VISIBLE);
        if (paperCard != null) paperCard.setVisibility(View.GONE);
        if (metalCard != null) metalCard.setVisibility(View.GONE);
    }

    private void showPaperOnly() {
        // Show only paper card
        if (plasticCard != null) plasticCard.setVisibility(View.GONE);
        if (paperCard != null) paperCard.setVisibility(View.VISIBLE);
        if (metalCard != null) metalCard.setVisibility(View.GONE);
    }
} 