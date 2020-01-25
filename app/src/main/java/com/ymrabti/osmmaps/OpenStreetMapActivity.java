package com.ymrabti.osmmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay;
import org.osmdroid.views.util.constants.MapViewConstants;

import java.util.ArrayList;
import java.util.Objects;

public class OpenStreetMapActivity extends AppCompatActivity implements  MapViewConstants {
    MapView map = null;
    ArrayList<OverlayItem> items;private IMapController mapControllers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(
                this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_osm);
        ActionBar actionBar= getSupportActionBar();actionBar.setTitle("Open Street Maps");
        items = new ArrayList<>();
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.OpenTopo);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapControllers = map.getController();


        LocationManager mLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (mLocMgr != null) {
            mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    GeoPoint gpt = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapControllers.setCenter(gpt);
                }
                @Override public void onProviderDisabled(String arg0) {
                    Toast.makeText(getApplicationContext(),arg0,Toast.LENGTH_LONG).show();
                }
                @Override public void onProviderEnabled(String provider) {
                    Toast.makeText(getApplicationContext(),provider,Toast.LENGTH_LONG).show();}
                @Override public void onStatusChanged(String provider, int status, Bundle extras) {
                    Toast.makeText(getApplicationContext(),provider+"\n"+status+"\n"+extras.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
        addOverlays();
        /*

        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),org.osmdroid.library.R.drawable.direction_arrow);
        locationOverlay.setPersonIcon(icon);
        locationOverlay.setPersonHotspot(icon.getWidth() / 2, icon.getHeight());
        map.getOverlays().add(locationOverlay);

        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            geoPoints.add(new LabelledGeoPoint(30 + Math.random() * 5, -8 + Math.random() * 5
                    , "Point #" + i));
        }
        Polyline line = new Polyline();
        line.setPoints(geoPoints);
        line.setColor(Color.RED);
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        map.getOverlayManager().add(line);
        Polygon polygon = new Polygon();
        polygon.setFillColor(Color.argb(100, 50,50,50));
        geoPoints.add(geoPoints.get(0));
        polygon.setPoints(geoPoints);
        polygon.setTitle("A sample polygon");

        List<List<GeoPoint>> holes = new ArrayList<>();
        holes.add(geoPoints);
        polygon.setHoles(holes);

        map.getOverlayManager().add(polygon);
        
        SimplePointTheme pt = new SimplePointTheme(points, true);

        Paint textStyle = new Paint();
        textStyle.setStyle(Paint.Style.FILL);
        textStyle.setColor(Color.parseColor("#0000ff"));
        textStyle.setTextAlign(Paint.Align.CENTER);
        textStyle.setTextSize(24);
        SimpleFastPointOverlayOptions opt = SimpleFastPointOverlayOptions.getDefaultStyle()
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(7).setIsClickable(true).setCellSize(15).setTextStyle(textStyle);
        final SimpleFastPointOverlay sfpo = new SimpleFastPointOverlay(pt, opt);
        sfpo.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
            @Override
            public void onClick(SimpleFastPointOverlay.PointAdapter points, Integer point) {
                Toast.makeText(map.getContext()
                        , "You clicked " + ((LabelledGeoPoint) points.get(point)).getLabel()
                        , Toast.LENGTH_SHORT).show();
            }
        });

        map.getOverlays().add(sfpo);*/
    }
    private void addOverlays(){

        mapControllers.setZoom(14);
        GeoPoint startPoint = new GeoPoint(33.547595, -7.650056);
        mapControllers.setCenter(startPoint);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setIcon(ResourcesCompat.getDrawable(getResources()
                , org.osmdroid.library.R.drawable.marker_default, null));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("START:\n" + startMarker.getPosition().toString()+"  " +startPoint.getAltitude());
        startMarker.setInfoWindow(new BasicInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, map));
        map.getOverlays().add(startMarker);

        for (int i = 0; i < 10; i++) {
            OverlayItem overlayItem = new OverlayItem("hire","simple"
                    ,new GeoPoint(30 + Math.random() * 3, -7.5 + Math.random() * 3));
            items.add(overlayItem);
        }

        ItemizedOverlay<OverlayItem> ItemsMarkers = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_LONG).show();
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, this);

        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(this);
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        mLocationOverlay.enableMyLocation();



        CompassOverlay mCompassOverlay = new CompassOverlay(this, map);
        mCompassOverlay.enableCompass();

        //ScaleBar scaleBar = new ScaleBar(this,map);map.getOverlays().add(scaleBar);
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);


        final DisplayMetrics dm = getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mScaleBarOverlay.setTextSize(26);

        MinimapOverlay mMinimapOverlay = new MinimapOverlay(this, map.getTileRequestCompleteHandler());
        mMinimapOverlay.setWidth(dm.widthPixels / 5);
        mMinimapOverlay.setHeight(dm.heightPixels / 5);


        SimpleLocationOverlay simpleLocationOverlay = new SimpleLocationOverlay(
                 ((BitmapDrawable) Objects.requireNonNull(getDrawable(org.osmdroid.library.R.drawable.moreinfo_arrow))).getBitmap());
        mapControllers.setCenter(simpleLocationOverlay.getMyLocation());

        CopyrightOverlay copyrightOverlay = new CopyrightOverlay(this);

        map.getOverlays().add(ItemsMarkers);
        map.getOverlays().add(mLocationOverlay);
        map.getOverlays().add(mCompassOverlay);
        //map.getOverlays().add(mRotationGestureOverlay);
        map.getOverlays().add(mScaleBarOverlay);
        map.getOverlays().add(mMinimapOverlay);
        map.getOverlays().add(simpleLocationOverlay);
        map.getOverlays().add(copyrightOverlay);
    }
    public void onResume(){ super.onResume(); }
    public void onPause(){ super.onPause(); }
}