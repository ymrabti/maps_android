package com.ymrabti.osmmaps.examles;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.here.sdk.core.GeoBox;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapviewlite.Camera;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapPolyline;
import com.here.sdk.mapviewlite.MapPolylineStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PixelFormat;
import com.here.sdk.routing.Maneuver;
import com.here.sdk.routing.ManeuverAction;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RouteLeg;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingEngine.CarOptions;
import com.here.sdk.routing.Waypoint;
import com.ymrabti.osmmaps.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class RoutingExample {

    private static final String TAG = RoutingExample.class.getName();

    private Context context;
    private MapViewLite mapView;
    private final List<MapMarker> mapMarkerList = new ArrayList<>();
    private final List<MapPolyline> mapPolylines = new ArrayList<>();
    private RoutingEngine routingEngine;
    private GeoCoordinates startGeoCoordinates;
    private GeoCoordinates destinationGeoCoordinates;

    public RoutingExample(Context context, MapViewLite mapView) {
        this.context = context;
        this.mapView = mapView;
        Camera camera = mapView.getCamera();
        camera.setTarget(new GeoCoordinates(52.520798, 13.409408));
        camera.setZoomLevel(12);

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }
    }

    public void addRoute() {
        clearMap();

        startGeoCoordinates = createRandomGeoCoordinatesInViewport();
        destinationGeoCoordinates = createRandomGeoCoordinatesInViewport();
        Waypoint startWaypoint = new Waypoint(startGeoCoordinates);
        Waypoint destinationWaypoint = new Waypoint(destinationGeoCoordinates);

        List<Waypoint> waypoints =
                new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

        routingEngine.calculateRoute(
                waypoints,
                new CarOptions(),
                (routingError, routes) -> {
                    if (routingError == null) {
                        Route route = routes.get(0);
                        showRouteDetails(route);
                        showRouteOnMap(route);
                    } else {
                        showDialog("Error while calculating a route:", routingError.toString());
                    }
                });
    }

    private void showRouteDetails(Route route) {
        int estimatedTravelTimeInSeconds = route.getTravelTimeInSeconds();
        int lengthInMeters = route.getLengthInMeters();

        String routeDetails =
                "Travel Time: " + formatTime(estimatedTravelTimeInSeconds)
                + ", Length: " + formatLength(lengthInMeters);

        showDialog("Route Details", routeDetails);
    }

    private String formatTime(int sec) {
        int hours = sec / 3600;
        int minutes = (sec % 3600) / 60;

        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    private String formatLength(int meters) {
        int kilometers = meters / 1000;
        int remainingMeters = meters % 1000;

        return String.format(Locale.getDefault(), "%02d.%02d km", kilometers, remainingMeters);
    }

    private void showRouteOnMap(Route route) {
        // Show route as polyline.
        GeoPolyline routeGeoPolyline;
        try {
            routeGeoPolyline = new GeoPolyline(route.getShape());
        } catch (InstantiationErrorException e) {
            // It should never happen that the route shape contains less than two vertices.
            return;
        }
        MapPolylineStyle mapPolylineStyle = new MapPolylineStyle();
        mapPolylineStyle.setColor(0x00908AA0, PixelFormat.RGBA_8888);
        mapPolylineStyle.setWidth(10);
        MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline, mapPolylineStyle);
        mapView.getMapScene().addMapPolyline(routeMapPolyline);
        mapPolylines.add(routeMapPolyline);

        // Draw a circle to indicate starting point and destination.
        addCircleMapMarker(startGeoCoordinates, R.drawable.green_dot);
        addCircleMapMarker(destinationGeoCoordinates, R.drawable.green_dot);

        // Log maneuver instructions per route leg.
        List<RouteLeg> routeLegs = route.getLegs();
        for (RouteLeg routeLeg : routeLegs) {
            logManeuverInstructions(routeLeg);
        }
    }

    private void logManeuverInstructions(RouteLeg routeLeg) {
        Timber.d("Log maneuver instructions per route leg:");
        List<Maneuver> maneuverInstructions = routeLeg.getManeuvers();
        for (Maneuver maneuverInstruction : maneuverInstructions) {
            ManeuverAction maneuverAction = maneuverInstruction.getAction();
            GeoCoordinates maneuverLocation = maneuverInstruction.getCoordinates();
            String maneuverInfo = maneuverInstruction.getText()
                    + ", Action: " + maneuverAction.name()
                    + ", Location: " + maneuverLocation.toString();
            Timber.d(maneuverInfo);
        }
    }

    public void addWaypoints() {
        if (startGeoCoordinates == null || destinationGeoCoordinates == null) {
            showDialog("Error", "Please add a route first.");
            return;
        }

        clearWaypointMapMarker();
        clearRoute();

        Waypoint waypoint1 = new Waypoint(createRandomGeoCoordinatesInViewport());
        Waypoint waypoint2 = new Waypoint(createRandomGeoCoordinatesInViewport());
        List<Waypoint> waypoints = new ArrayList<>(Arrays.asList(new Waypoint(startGeoCoordinates),
                waypoint1, waypoint2, new Waypoint(destinationGeoCoordinates)));

        routingEngine.calculateRoute(
                waypoints,
                new CarOptions(),
                (routingError, routes) -> {
                    if (routingError == null) {
                        Route route = routes.get(0);
                        showRouteDetails(route);
                        showRouteOnMap(route);

                        // Draw a circle to indicate the location of the waypoints.
                        addCircleMapMarker(waypoint1.coordinates, R.drawable.red_dot);
                        addCircleMapMarker(waypoint2.coordinates, R.drawable.red_dot);
                    } else {
                        showDialog("Error while calculating a route:", routingError.toString());
                    }
                });
    }

    public void clearMap() {
        clearWaypointMapMarker();
        clearRoute();
    }

    private void clearWaypointMapMarker() {
        for (MapMarker mapMarker : mapMarkerList) {
            mapView.getMapScene().removeMapMarker(mapMarker);
        }
        mapMarkerList.clear();
    }

    private void clearRoute() {
        for (MapPolyline mapPolyline : mapPolylines) {
            mapView.getMapScene().removeMapPolyline(mapPolyline);
        }
        mapPolylines.clear();
    }

    private GeoCoordinates createRandomGeoCoordinatesInViewport() {
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

    private void addCircleMapMarker(GeoCoordinates geoCoordinates, int resourceId) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), resourceId);
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
