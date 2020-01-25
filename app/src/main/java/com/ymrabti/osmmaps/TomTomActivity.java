package com.ymrabti.osmmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.tomtom.online.sdk.common.util.LogUtils;
import com.tomtom.online.sdk.map.MapView;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.model.MapTilesType;

public class TomTomActivity extends AppCompatActivity  {
    private final static String LOG_FILE_PATH = "logs_tom_tom";
    private TomtomMap tomtomMap;

    private final OnMapReadyCallback onMapReadyCallback =
            map -> {
                tomtomMap = map;
                tomtomMap.setMyLocationEnabled(true);
                Toast toast =  Toast.makeText(getApplicationContext(),"map setted",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,20,20);
                toast.show();
                tomtomMap.collectLogsToFile(TomTomActivity.LOG_FILE_PATH);
                tomtomMap.getUiSettings().setMapTilesType(MapTilesType.VECTOR);
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tom_tom);
        LogUtils.enableLogs(Log.VERBOSE);
        LogUtils.collectLogsToFile(LOG_FILE_PATH);
        MapView mapView = findViewById(R.id.tomtom_map_);
        mapView.addOnMapReadyCallback(onMapReadyCallback);
        mapView.setOnClickListener(v -> Toast.makeText(getApplicationContext(),"map setted",Toast.LENGTH_LONG).show());
        ActionBar actionBar= getSupportActionBar();actionBar.setTitle("Tom Tom Maps");
        actionBar.setIcon(android.R.drawable.ic_menu_slideshow);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tomtomMap.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}