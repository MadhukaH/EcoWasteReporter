package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.tabs.TabLayout;

public class RecyclingTipsPage extends AppCompatActivity {
    private TabLayout tabLayout;
    private CardView plasticCard, paperCard, metalCard, glassCard, ewasteCard, organicCard;
    private LayoutAnimationController animationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_tips_page);

        initializeViews();
        setupTabLayout();
        setupAnimations();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        plasticCard = findViewById(R.id.plasticCard);
        paperCard = findViewById(R.id.paperCard);
        metalCard = findViewById(R.id.metalCard);
        glassCard = findViewById(R.id.glassCard);
        ewasteCard = findViewById(R.id.ewasteCard);
        organicCard = findViewById(R.id.organicCard);

        // Add tabs
        tabLayout.addTab(tabLayout.newTab().setText("All Tips"));
        tabLayout.addTab(tabLayout.newTab().setText("Plastic"));
        tabLayout.addTab(tabLayout.newTab().setText("Paper"));
        tabLayout.addTab(tabLayout.newTab().setText("Metal"));
        tabLayout.addTab(tabLayout.newTab().setText("Glass"));
        tabLayout.addTab(tabLayout.newTab().setText("E-Waste"));
        tabLayout.addTab(tabLayout.newTab().setText("Organic"));
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                animateCards(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                animateCards(tab.getPosition());
            }
        });
    }

    private void setupAnimations() {
        animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        // Apply initial animation
        animateCards(0);
    }

    private void animateCards(int tabPosition) {
        // First hide all cards with fade out animation
        fadeOutCards();

        // Show appropriate cards with animation after a short delay
        tabLayout.postDelayed(() -> {
            switch (tabPosition) {
                case 0: // All Tips
                    showAllCards();
                    break;
                case 1: // Plastic
                    showPlasticOnly();
                    break;
                case 2: // Paper
                    showPaperOnly();
                    break;
                case 3: // Metal
                    showMetalOnly();
                    break;
                case 4: // Glass
                    showGlassOnly();
                    break;
                case 5: // E-Waste
                    showEWasteOnly();
                    break;
                case 6: // Organic
                    showOrganicOnly();
                    break;
            }
        }, 200); // Short delay for smooth transition
    }

    private void fadeOutCards() {
        if (plasticCard.getVisibility() == View.VISIBLE) {
            plasticCard.animate().alpha(0f).setDuration(200);
        }
        if (paperCard.getVisibility() == View.VISIBLE) {
            paperCard.animate().alpha(0f).setDuration(200);
        }
        if (metalCard.getVisibility() == View.VISIBLE) {
            metalCard.animate().alpha(0f).setDuration(200);
        }
        if (glassCard.getVisibility() == View.VISIBLE) {
            glassCard.animate().alpha(0f).setDuration(200);
        }
        if (ewasteCard.getVisibility() == View.VISIBLE) {
            ewasteCard.animate().alpha(0f).setDuration(200);
        }
        if (organicCard.getVisibility() == View.VISIBLE) {
            organicCard.animate().alpha(0f).setDuration(200);
        }
    }

    private void showAllCards() {
        showCardWithAnimation(plasticCard);
        showCardWithAnimation(paperCard);
        showCardWithAnimation(metalCard);
        showCardWithAnimation(glassCard);
        showCardWithAnimation(ewasteCard);
        showCardWithAnimation(organicCard);
    }

    private void showPlasticOnly() {
        showCardWithAnimation(plasticCard);
        paperCard.setVisibility(View.GONE);
        metalCard.setVisibility(View.GONE);
        glassCard.setVisibility(View.GONE);
        ewasteCard.setVisibility(View.GONE);
        organicCard.setVisibility(View.GONE);
    }

    private void showPaperOnly() {
        plasticCard.setVisibility(View.GONE);
        showCardWithAnimation(paperCard);
        metalCard.setVisibility(View.GONE);
        glassCard.setVisibility(View.GONE);
        ewasteCard.setVisibility(View.GONE);
        organicCard.setVisibility(View.GONE);
    }

    private void showMetalOnly() {
        plasticCard.setVisibility(View.GONE);
        paperCard.setVisibility(View.GONE);
        showCardWithAnimation(metalCard);
        glassCard.setVisibility(View.GONE);
        ewasteCard.setVisibility(View.GONE);
        organicCard.setVisibility(View.GONE);
    }

    private void showGlassOnly() {
        plasticCard.setVisibility(View.GONE);
        paperCard.setVisibility(View.GONE);
        metalCard.setVisibility(View.GONE);
        showCardWithAnimation(glassCard);
        ewasteCard.setVisibility(View.GONE);
        organicCard.setVisibility(View.GONE);
    }

    private void showEWasteOnly() {
        plasticCard.setVisibility(View.GONE);
        paperCard.setVisibility(View.GONE);
        metalCard.setVisibility(View.GONE);
        glassCard.setVisibility(View.GONE);
        showCardWithAnimation(ewasteCard);
        organicCard.setVisibility(View.GONE);
    }

    private void showOrganicOnly() {
        plasticCard.setVisibility(View.GONE);
        paperCard.setVisibility(View.GONE);
        metalCard.setVisibility(View.GONE);
        glassCard.setVisibility(View.GONE);
        ewasteCard.setVisibility(View.GONE);
        showCardWithAnimation(organicCard);
    }

    private void showCardWithAnimation(CardView card) {
        card.setVisibility(View.VISIBLE);
        card.setAlpha(0f);
        card.animate()
            .alpha(1f)
            .setDuration(300)
            .start();
        
        // Apply fall down animation
        card.startAnimation(AnimationUtils.loadAnimation(this, R.anim.item_animation_fall_down));
    }
} 