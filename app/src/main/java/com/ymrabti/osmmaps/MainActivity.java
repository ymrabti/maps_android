package com.ymrabti.osmmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
        arcgis.setOnClickListener(v -> openActivity(ArcgisActivity.class));
        arscene.setOnClickListener(v -> openActivity(ArcGISscesneActivity.class));
        osm.setOnClickListener(v -> openActivity(OpenStreetMapActivity.class));
        tomtom.setOnClickListener(v -> openActivity(TomTomActivity.class));
        leaflet.setOnClickListener(v -> openActivity(LeafletActivity.class));
        bing.setOnClickListener(v -> openActivity(BingActivity.class));
        google.setOnClickListener(v -> openActivity(GoogleMapsActivity.class));
        here.setOnClickListener(v -> openActivity(HereActivity.class));
    }
    private void openActivity(Class classse){
        Intent intent = new Intent(getApplicationContext(),classse);
        startActivity(intent);
    }

}
