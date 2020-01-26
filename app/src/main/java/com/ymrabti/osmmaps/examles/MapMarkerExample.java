package com.ymrabti.osmmaps.examles;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoBox;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.mapviewlite.Camera;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.ymrabti.osmmaps.R;

import java.util.ArrayList;
import java.util.List;

public class MapMarkerExample {

    private Context context;
    private MapViewLite mapView;
    private final List<MapMarker> mapMarkerList = new ArrayList<>();

    public MapMarkerExample(Context context, MapViewLite mapView) {
        this.context = context;
        this.mapView = mapView;
        Camera camera = mapView.getCamera();
        camera.setTarget(new GeoCoordinates(33.547595, -7.650056));
        camera.setZoomLevel(15);

        // Setting a tap handler to pick markers from map
        setTapGestureHandler();

        Toast.makeText(context,"You can tap markers.", Toast.LENGTH_LONG).show();
    }

    public void showAnchoredMapMarkers() {
        for (int i = 0; i < 10; i++) {
            GeoCoordinates geoCoordinates = createRandomLatLonInViewport();

            // Centered on location. Shown below the POI image to indicate the location.
            addCircleMapMarker(geoCoordinates);

            // Anchored, pointing to location.
            addPOIMapMarker(geoCoordinates);
        }
    }

    public void showCenteredMapMarkers() {
        GeoCoordinates geoCoordinates = createRandomLatLonInViewport();

        // Centered on location.
        addPhotoMapMarker(geoCoordinates);

        // Centered on location. Shown on top of the previous image to indicate the location.
        addCircleMapMarker(geoCoordinates);
    }

    public void clearMap() {
        for (MapMarker mapMarker : mapMarkerList) {
            mapView.getMapScene().removeMapMarker(mapMarker);
        }
        mapMarkerList.clear();
    }

    private GeoCoordinates createRandomLatLonInViewport() {
        GeoBox geoBox = mapView.getCamera().getBoundingRect();
        GeoCoordinates northEast = geoBox.northEastCorner;
        GeoCoordinates southWest = geoBox.southWestCorner;

        double minLat = southWest.latitude;
        double maxLat = northEast.latitude;
        double lat = getRandom(minLat, maxLat);

        double minLon = southWest.longitude;
        double maxLon = northEast.longitude;
        double lon = getRandom(minLon, maxLon);

        return new GeoCoordinates(lat, lon);
    }

    private double getRandom(double min, double max) {
        return min + Math.random() * (max - min);
    }

    private void setTapGestureHandler() {
        mapView.getGestures().setTapListener(this::pickMapMarker);
    }

    private void pickMapMarker(final Point2D touchPoint) {
        float radiusInPixel = 2;
        mapView.pickMapItems(touchPoint, radiusInPixel, pickMapItemsResult -> {
            if (pickMapItemsResult == null) {
                return;
            }

            MapMarker topmostMapMarker = pickMapItemsResult.getTopmostMarker();
            if (topmostMapMarker == null) {
                return;
            }

            Metadata metadata = topmostMapMarker.getMetadata();
            if (metadata != null) {
                String message = "No message found.";
                String string = metadata.getString("key_poi");
                if (string != null) {
                    message = string;
                }
                showDialog("Map Marker picked", message);
                return;
            }

            showDialog("Map marker picked:", "Location: " +
                    topmostMapMarker.getCoordinates().latitude + ", " +
                    topmostMapMarker.getCoordinates().longitude);
        });
    }

    private void addPOIMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.poi);

        MapMarker mapMarker = new MapMarker(geoCoordinates);

        // The bottom, middle position should point to the location.
        // By default, the anchor point is set to 0.5, 0.5.
        MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
        mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5F, 1));

        mapMarker.addImage(mapImage, mapMarkerImageStyle);

        Metadata metadata = new Metadata();
        metadata.setString("key_poi", "This is a POI.");
        mapMarker.setMetadata(metadata);

        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void addPhotoMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.circle);

        MapMarker mapMarker = new MapMarker(geoCoordinates);
        mapMarker.addImage(mapImage, new MapMarkerImageStyle());

        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void addCircleMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.circle);

        MapMarker mapMarker = new MapMarker(geoCoordinates);
        mapMarker.addImage(mapImage, new MapMarkerImageStyle());

        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
