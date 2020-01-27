package com.ymrabti.osmmaps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.maps.MapProjection;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapTappedEventArgs;
import com.microsoft.maps.MapView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;
import org.jetbrains.annotations.NotNull;
import com.microsoft.maps.MapStyleSheets;
import com.microsoft.maps.OnMapTappedListener;
import com.ymrabti.osmmaps.tests.ClassWithInterface;


import java.util.Objects;

public class BingActivity extends AppCompatActivity {
    private MapElementLayer mPinLayer;
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
        mPinLayer = new MapElementLayer();
        mMapView.getLayers().add(mPinLayer);
        Geopoint location = new Geopoint(33.547595,-7.650056);
        String title =  "marker micro soft";
        Bitmap pinBitmap = ((BitmapDrawable) Objects.requireNonNull(getDrawable(R.drawable.circle))).getBitmap() ;
        MapIcon pushpin = new MapIcon();
        pushpin.setLocation(location);
        pushpin.setTitle(title);
        pushpin.setImage(new MapImage(pinBitmap));
        mPinLayer.getElements().add(pushpin);
        mMapView.setMapStyleSheet(MapStyleSheets.roadDark());
        mMapView.setMapProjection(MapProjection.GLOBE);

        mMapView.addOnMapTappedListener(mapTappedEventArgs -> {

            MapIcon pus_hpin = new MapIcon();
            pus_hpin.setLocation(mapTappedEventArgs.location);
            pus_hpin.setTitle("marker tapped");
            pus_hpin.setImage(new MapImage(((BitmapDrawable)
                    Objects.requireNonNull(getDrawable(R.drawable.green_dot))).getBitmap()));
            mPinLayer.getElements().add(pus_hpin);

            ClassWithInterface classWithInterface = new ClassWithInterface(2);
            classWithInterface.request(new ClassWithInterface.ResultListener() {
                @Override
                public void younes() {
                    Toast.makeText(BingActivity.this,"younes",Toast.LENGTH_LONG).show();
                }

                @Override
                public void mrabti() {
                    Toast.makeText(BingActivity.this,"mrabti",Toast.LENGTH_LONG).show();
                }
            });
            return true;
        });
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
