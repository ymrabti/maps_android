package com.ymrabti.osmmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tomtom.online.sdk.common.util.LogUtils;
import com.tomtom.online.sdk.map.MapView;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.TomtomMap;

public class TomTomActivity extends AppCompatActivity  {
    private MapView mapView;
    private final static String LOG_FILE_PATH = "logs_tom_tom";
    private TomtomMap tomtomMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tom_tom);
        LogUtils.enableLogs(Log.VERBOSE);
        LogUtils.collectLogsToFile(LOG_FILE_PATH);
        mapView=findViewById(R.id.tomtom_map_);
        new Thread(() -> {
            final OnMapReadyCallback onMapReadyCallback =
                    map -> {
                        tomtomMap = map;
                        tomtomMap.setMyLocationEnabled(true);
                        tomtomMap.collectLogsToFile(TomTomActivity.LOG_FILE_PATH);
                    };
            mapView.addOnMapReadyCallback(onMapReadyCallback);
        }).start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tomtomMap.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}