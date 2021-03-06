package com.ymrabti.osmmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceLikelihood;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import timber.log.Timber;

import static com.google.android.libraries.places.compat.Places.getPlaceDetectionClient;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mapView;private LatLng current;
    private GoogleMap gmap;
    private static final String TAG = GoogleMapsActivity.class.getSimpleName();
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        ActionBar actionBar= getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Google Maps");
        }
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        mPlaceDetectionClient = getPlaceDetectionClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    /**/current= new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions current_position=new MarkerOptions();
                    current_position.position(current);
                    gmap.addMarker(current_position);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        FloatingActionButton fb = findViewById(R.id.change_basemap);
        fb.setOnClickListener(v -> popup());
    }
    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (gmap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, gmap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override protected void onResume() { super.onResume();mapView.onResume(); }
    @Override protected void onStart() { super.onStart();mapView.onStart(); }
    @Override public void onStop() { super.onStop();mapView.onStop(); }
    @Override public void onPause() { mapView.onPause();super.onPause(); }
    @Override public void onDestroy() { mapView.onDestroy();super.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory();mapView.onLowMemory(); }
    @Override public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.activity_google_maps,
                        findViewById(R.id.map_container), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                return infoWindow;
            }
        });
        gmap.setMinZoomPreference(1);
        gmap.setIndoorEnabled(true);

        UiSettings uiSettings = gmap.getUiSettings();

        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }
    private void popup(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_up_layers);
        TextView normale = dialog.findViewById(R.id.normale);
        TextView hybrid = dialog.findViewById(R.id.hybrid);
        TextView satellite = dialog.findViewById(R.id.satellite);
        TextView terrain = dialog.findViewById(R.id.terrain);
        TextView nonee = dialog.findViewById(R.id.nonee);
        normale.setOnClickListener(v -> {
            gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            dialog.dismiss();
        });
        hybrid.setOnClickListener(v -> {
            gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            dialog.dismiss();
        });
        satellite.setOnClickListener(v -> {
            gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            dialog.dismiss();
        });
        terrain.setOnClickListener(v -> {
            gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            dialog.dismiss();
        });
        nonee.setOnClickListener(v -> {
            gmap.setMapType(GoogleMap.MAP_TYPE_NONE);
            dialog.dismiss();
        });
        dialog.show();
    }
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        assert mLastKnownLocation != null;
                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Timber.tag(TAG).d("Current location is null. Using defaults.");
                        Timber.tag(TAG).e(task.getException(), "Exception: ");
                        gmap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e)  {
            Timber.tag("Exception: ").e(e.toString());
        }
    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(
                this.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA
                    },
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
        updateLocationUI();showCurrentPlace();
    }
    private void updateLocationUI() {
        if (gmap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                gmap.setMyLocationEnabled(true);
                gmap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                gmap.setMyLocationEnabled(false);
                gmap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Timber.tag("Exception: ").e(e);
        }
    }
    private void showCurrentPlace() {
        if (gmap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            @SuppressWarnings("MissingPermission")
            final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                count = likelyPlaces.getCount();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new String[count];
                            mLikelyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                        .getAddress();
                                mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                        .getAttributions();
                                mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }

                            likelyPlaces.release();
                            openPlacesDialog();

                        } else {
                            Timber.tag(TAG).e(task.getException(), "Exception: ");
                        }
                    });
        } else {
            Timber.tag(TAG).i("The user did not grant location permission.");
            getLocationPermission();
        }
    }
    private void openPlacesDialog() {
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            LatLng markerLatLng = mLikelyPlaceLatLngs[which];
            String markerSnippet = mLikelyPlaceAddresses[which];
            if (mLikelyPlaceAttributions[which] != null) {
                markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
            }

            gmap.addMarker(new MarkerOptions()
                    .title(mLikelyPlaceNames[which])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                    DEFAULT_ZOOM));
        };

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener).create();
        dialog.show();
    }
}
