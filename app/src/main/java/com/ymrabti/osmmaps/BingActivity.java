package com.ymrabti.osmmaps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapView;
import android.os.Bundle;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

public class BingActivity extends AppCompatActivity {
    private MapView mMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bing);
        ActionBar actionBar= getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Bing Maps");
        }
        mMapView = new MapView(this, MapRenderMode.VECTOR);
        mMapView.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
        ((FrameLayout)findViewById(R.id.map_view_bing)).addView(mMapView);
        mMapView.onCreate(savedInstanceState);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
