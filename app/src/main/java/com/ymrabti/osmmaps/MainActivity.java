package com.ymrabti.osmmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button arcgis = findViewById(R.id.arcgis_map);
        Button arscene = findViewById(R.id.arcgis_scene);
        Button osm = findViewById(R.id.osm);
        Button leaflet = findViewById(R.id.Leaflet_maps);
        Button bing = findViewById(R.id.bing_maps);
        Button tomtom = findViewById(R.id.tomtom_map);
        Button google = findViewById(R.id.google_map);
        Button here = findViewById(R.id.here_map);
        arcgis.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),ArcgisActivity.class);
            startActivity(intent);
        });
        arscene.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),ArcGISscesneActivity.class);
            startActivity(intent);
        });
        osm.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),OpenStreetMapActivity.class);
            startActivity(intent);
        });
        View.OnClickListener clickListener = v -> Toast.makeText(getApplicationContext(), "coming soon !", Toast.LENGTH_LONG).show();
        leaflet.setOnClickListener(clickListener);
        bing.setOnClickListener(clickListener);
        tomtom.setOnClickListener(clickListener);
        google.setOnClickListener(clickListener);
        here.setOnClickListener(clickListener);
    }

}
