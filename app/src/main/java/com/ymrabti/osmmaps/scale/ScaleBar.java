package com.ymrabti.osmmaps.scale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Picture;
import android.graphics.Rect;
import android.location.Location;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import static java.lang.Math.round;

public class ScaleBar extends Overlay {
    private boolean enabled = true;

    private final MapView mapView;

    private final Picture scaleBarPicture = new Picture();
    private final Matrix scaleBarMatrix = new Matrix();


    private float xdpi;
    private float ydpi;
    private int screenWidth;
    private int screenHeight;

    public ScaleBar(Context _context, MapView mapView) {
        super();
        this.mapView = mapView;

        xdpi = _context.getResources().getDisplayMetrics().xdpi;
        ydpi = _context.getResources().getDisplayMetrics().ydpi;

        screenWidth = _context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = _context.getResources().getDisplayMetrics().heightPixels;

    }

    @Override
    public void draw(Canvas canvas, MapView osmv, boolean shadow) {
        if (enabled) {
            if (!shadow) {
                createScaleBarPicture();
                scaleBarMatrix.setTranslate(
                        -1 * (scaleBarPicture.getWidth() / 2 - 0.25f),
                        -1 * (scaleBarPicture.getHeight() / 2 - 0.25f));
                scaleBarMatrix.postTranslate(xdpi, ydpi/2 );

                canvas.save();
                canvas.setMatrix(scaleBarMatrix);
                canvas.drawPicture(scaleBarPicture);
                canvas.restore();
            }
        }

    }
    private void createScaleBarPicture() {

        float xMetersPerInch = MetersPerInch(xdpi);

        final Paint barPaint = new Paint();
        final Paint textPaint = new Paint();
        barPaint.setColor(Color.BLACK);
        textPaint.setColor(Color.BLACK);
        barPaint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        barPaint.setStyle(Style.FILL);
        textPaint.setStyle(Style.FILL);
        barPaint.setAlpha(255);
        textPaint.setAlpha(255);

        int textSize = 18;
        textPaint.setTextSize(textSize);

        final Canvas canvas = scaleBarPicture.beginRecording((int)xdpi, (int)ydpi);

        float xOffset = 10;
        float yOffset = 10;

        String Msg = scaleBarLengthText(xMetersPerInch/*, imperial, nautical*/);
        Rect TextRect = new Rect();
        textPaint.getTextBounds(Msg, 0, Msg.length(), TextRect);

        int textSpacing = (int)(TextRect.height() / 5.0);

        float lineWidth = 2;
        canvas.drawRect(xOffset+0, yOffset+0, xOffset + xdpi,
                yOffset + lineWidth, barPaint);

        canvas.drawRect(xOffset + xdpi, yOffset+0, xOffset + xdpi + lineWidth,
                yOffset + TextRect.height() + lineWidth + textSpacing, barPaint);

        canvas.drawRect(xOffset+0     , yOffset+0, xOffset + lineWidth,
                yOffset + TextRect.height() + lineWidth + textSpacing, barPaint);

        canvas.drawText(Msg+""
                , (xOffset + xdpi/2 - (float) TextRect.width()/2)
                , (yOffset + TextRect.height() + lineWidth + textSpacing), textPaint);


        scaleBarPicture.endRecording();
    }

    private String scaleBarLengthText(float meters) {
        String scale = round(meters)+"";
        int k = scale.length();
        return scale + " m"+k;
    }

    private float MetersPerInch(float dx){
        Projection projection = mapView.getProjection();

        Location locationP1 = new Location("ScaleBar location p1");
        Location locationP2 = new Location("ScaleBar location p2");

        IGeoPoint p1 = projection.fromPixels((int) ((screenWidth / 2) - (dx / 2)), screenHeight / 2);
        IGeoPoint p2 = projection.fromPixels((int) ((screenWidth / 2) + (dx / 2)), screenHeight / 2);

        locationP1.setLatitude (p1.getLatitude() );
        locationP1.setLongitude(p1.getLongitude());

        locationP2.setLatitude (p2.getLatitude() );
        locationP2.setLongitude(p2.getLongitude());

        return locationP1.distanceTo(locationP2);
    }


    public boolean isEnabled() {
        return enabled;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
