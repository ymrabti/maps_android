package com.ymrabti.osmmaps.examles;

import android.content.Context;
import android.widget.Toast;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.gestures.GestureState;
import com.here.sdk.gestures.GestureType;
import com.here.sdk.mapviewlite.Camera;
import com.here.sdk.mapviewlite.MapViewLite;

import timber.log.Timber;

public class GesturesExample {

     private static final String TAG = GesturesExample.class.getSimpleName();

     private final GestureMapAnimator gestureMapAnimator;

     public GesturesExample(Context context, MapViewLite mapView) {
         Camera camera = mapView.getCamera();
         camera.setTarget(new GeoCoordinates(52.530932, 13.384915));
         camera.setZoomLevel(14);

         gestureMapAnimator = new GestureMapAnimator(mapView);

         setTapGestureHandler(mapView);
         setDoubleTapGestureHandler(mapView);
         setTwoFingerTapGestureHandler(mapView);
         setLongPressGestureHandler(mapView);

         // Disable the default map gesture behavior for DoubleTap (zooms in) and TwoFingerTap (zooms out)
         // as we want to enable custom map animations when such gestures are detected.
         mapView.getGestures().disableDefaultAction(GestureType.DOUBLE_TAP);
         mapView.getGestures().disableDefaultAction(GestureType.TWO_FINGER_TAP);

         Toast.makeText(context, "Shows Tap and LongPress gesture handling. " +
                 "See log for details. DoubleTap / TwoFingerTap map action (zoom in/out) is disabled " +
                 "and replaced with a custom animation.", Toast.LENGTH_LONG).show();
     }

     private void setTapGestureHandler(MapViewLite mapView) {
         mapView.getGestures().setTapListener(touchPoint -> {
             GeoCoordinates geoCoordinates = mapView.getCamera().viewToGeoCoordinates(touchPoint);
             Timber.tag(TAG).d("Tap at: %s", geoCoordinates);
         });
     }

     private void setDoubleTapGestureHandler(MapViewLite mapView) {
         mapView.getGestures().setDoubleTapListener(touchPoint -> {
             GeoCoordinates geoCoordinates = mapView.getCamera().viewToGeoCoordinates(touchPoint);
             Timber.d("Default zooming in is disabled. DoubleTap at: %s", geoCoordinates);

             // Start our custom zoom in animation.
             gestureMapAnimator.zoomIn();
         });
     }

     private void setTwoFingerTapGestureHandler(MapViewLite mapView) {
         mapView.getGestures().setTwoFingerTapListener(touchCenterPoint -> {
             GeoCoordinates geoCoordinates = mapView.getCamera().viewToGeoCoordinates(touchCenterPoint);
             Timber.d("Default zooming in is disabled. TwoFingerTap at: %s", geoCoordinates);

             // Start our custom zoom out animation.
             gestureMapAnimator.zoomOut();
         });
     }

     private void setLongPressGestureHandler(MapViewLite mapView) {
         mapView.getGestures().setLongPressListener((gestureState, touchPoint) -> {
             GeoCoordinates geoCoordinates = mapView.getCamera().viewToGeoCoordinates(touchPoint);

             if (gestureState == GestureState.BEGIN) {
                 Timber.d("LongPress detected at: %s", geoCoordinates);
             }

             if (gestureState == GestureState.UPDATE) {
                 Timber.d("LongPress update at: %s", geoCoordinates);
             }

             if (gestureState == GestureState.END) {
                 Timber.d("LongPress finger lifted at: %s", geoCoordinates);
             }
         });
     }

     // This is just an example how to clean up.
     @SuppressWarnings("unused")
     private void removeGestureHandler(MapViewLite mapView) {
         // Stop listening.
         mapView.getGestures().setTapListener(null);
         mapView.getGestures().setDoubleTapListener(null);
         mapView.getGestures().setTwoFingerTapListener(null);
         mapView.getGestures().setLongPressListener(null);

         // Bring back the default map gesture behavior for DoubleTap (zooms in)
         // and TwoFingerTap (zooms out). These actions were disabled above.
         mapView.getGestures().enableDefaultAction(GestureType.DOUBLE_TAP);
         mapView.getGestures().enableDefaultAction(GestureType.TWO_FINGER_TAP);
     }
}
