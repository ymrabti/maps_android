package com.ymrabti.osmmaps;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;

import timber.log.Timber;

public class HereActivity extends AppCompatActivity {
    private MapViewLite mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_here);
        ActionBar actionBar= getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Here Maps");
        }
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        loadMapScene();
    }
    private void loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, errorCode -> {
            if (errorCode == null) {
                mapView.getCamera().setTarget(new GeoCoordinates(52.530932, 13.384915));
                mapView.getCamera().setZoomLevel(14);
            } else {
                Timber.tag("exc").d("onLoadScene failed: %s", errorCode.toString());
            }
        });
    }
}
