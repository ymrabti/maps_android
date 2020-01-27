package com.ymrabti.osmmaps;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.ymrabti.osmmaps.examles.MapMarkerExample;
import com.ymrabti.osmmaps.examles.MapObjectsExample;
import com.ymrabti.osmmaps.permession.PermissionsRequestor;

import timber.log.Timber;

public class HereActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private PermissionsRequestor permissionsRequestor;
    private MapMarkerExample mapMarkerExample;
    private MapObjectsExample mapObjectsExample;
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
        handleAndroidPermissions();
    }
    public void anchoredMapMarkersButtonClicked(View view) {
        mapMarkerExample.showAnchoredMapMarkers();
    }

    public void centeredMapMarkersButtonClicked(View view) {
        mapMarkerExample.showCenteredMapMarkers();
    }

    public void clearMapButtonClicked(View view) {
        mapMarkerExample.clearMap();
    }




    public void onMapPolylineButtonClicked(View view) {
        mapObjectsExample.showMapPolyline();
    }

    public void onMapPolygonButtonClicked(View view) {
        mapObjectsExample.showMapPolygon();
    }

    public void onMapCircleButtonClicked(View view) {
        mapObjectsExample.showMapCircle();
    }



    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Timber.tag(TAG).e("Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }
    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, errorCode -> {
            if (errorCode == null) {
                mapView.getCamera().setTarget(new GeoCoordinates(33.547595, -7.650056));
                mapView.getCamera().setZoomLevel(14);
                mapObjectsExample = new MapObjectsExample(mapView);
                mapMarkerExample = new MapMarkerExample(HereActivity.this, mapView);
            } else {
                Timber.tag("exc").d("onLoadScene failed: %s", errorCode.toString());
            }
        });
    }
}
