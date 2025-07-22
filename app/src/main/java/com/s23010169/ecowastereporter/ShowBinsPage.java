package com.s23010169.ecowastereporter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010169.ecowastereporter.adapters.BinAdapter;
import com.s23010169.ecowastereporter.models.Bin;
import java.util.ArrayList;
import java.util.List;

public class ShowBinsPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bins_page);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowBins);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        List<Bin> bins = new ArrayList<>();
        bins.add(new Bin("Matara Bus Stand Recycling Point", 45, 0.2, 5.9549, 80.5550));
        bins.add(new Bin("Uyanwatta Stadium Waste Bin", 30, 0.5, 5.9539, 80.5535));
        bins.add(new Bin("Matara Beach Recycling Center", 75, 0.8, 5.9485, 80.5453));
        bins.add(new Bin("Star Fort Waste Collection", 25, 1.0, 5.9527, 80.5477));
        bins.add(new Bin("Matara Central Market Bin", 90, 0.7, 5.9520, 80.5424));
        bins.add(new Bin("Nupe Junction Recycling Point", 60, 1.2, 5.9572, 80.5506));
        bins.add(new Bin("Rahula College Area Bin", 40, 0.9, 5.9563, 80.5559));
        bins.add(new Bin("Matara Railway Station Bin", 55, 0.6, 5.9515, 80.5443));
        bins.add(new Bin("Sanath Jayasuriya Ground Bin", 35, 1.1, 5.9533, 80.5518));
        bins.add(new Bin("Matara Hospital Waste Point", 85, 0.4, 5.9508, 80.5435));
        BinAdapter adapter = new BinAdapter(this, bins, null);
        recyclerView.setAdapter(adapter);
    }
} 