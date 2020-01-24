package com.ymrabti.osmmaps.scale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.text.TextPaint;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

public class CopyOfScaleBarOverlay extends Overlay {
    private static final String STR_M = "m";
    private static final String STR_KM = "km";


    private Paint paintLine, paintText, paintRectangle;
    private Location l0;
    private Location l1;
    private float ds;
    private int width, height, pi;
    private float marginLeft, marginTop, marginBottom, lineTopSize;


    public CopyOfScaleBarOverlay(Context context){
        super();
        //instantiation

        paintText= new TextPaint();
        paintText.setARGB(180, 0, 0, 0);
        paintText.setAntiAlias(true);
        paintText.setTextAlign(Paint.Align.CENTER);

        paintRectangle = new Paint();
        paintRectangle.setARGB(80,255,255,255);
        paintRectangle.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setARGB(180, 0, 0, 0);
        paintLine.setAntiAlias(true);

        l0 = new Location("none");
        l1 = new Location("none");

        ds= context.getApplicationContext().getResources().getDisplayMetrics().density;
        width= context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        height= context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        float distanceFromBottom = 100;
        pi = (int) (height - distanceFromBottom *ds);

        float cMarginLeft = 4;
        marginLeft= cMarginLeft *ds;
        float cLineTopSize = 8;
        lineTopSize= cLineTopSize *ds;
        float cMarginTop = 6;
        marginTop= cMarginTop *ds;
        float cMarginBottom = 2;
        marginBottom= cMarginBottom *ds;


    }

    @Override
    public void draw(Canvas canvas, MapView mapview, boolean shadow) {
        if(mapview.getZoomLevel() > 1){

            //Calculate scale bar size and units
            IGeoPoint g0 = mapview.getProjection().fromPixels(0, height/2);
            IGeoPoint g1 = mapview.getProjection().fromPixels(width, height/2);
            l0.setLatitude(g0.getLatitude());
            l0.setLongitude(g0.getLongitude());
            l1.setLatitude(g1.getLatitude());
            l1.setLongitude(g1.getLongitude());
            float d01=l0.distanceTo(l1);
            //Constants
            float scaleBarProportion = 0.25f;
            float d02=d01* scaleBarProportion;
            // multiply d02 by a unit conversion factor if needed
            float cd02;
            String unit;
            if(d02 > 1000){
                unit = STR_KM;
                cd02 = d02 / 1000;
            } else{
                unit = STR_M;
                cd02 = d02;
            }
            int i=1;
            do{
                i *=10;
            }while (i <= cd02);
            i/=10;
            float dcd02=(int)(cd02/i)*i;
            float bs=dcd02*width/d01*d02/cd02;

            String text=String.format("%.0f %s", dcd02, unit);
            float cTextSize = 12;
            paintText.setTextSize(cTextSize * ds);
            float text_x_size=paintText.measureText(text);
            float x_size = bs + text_x_size/2 + 2*marginLeft;

            //Draw rectangle
            canvas.drawRect(0,pi,x_size,pi+marginTop+paintText.getFontSpacing()+marginBottom, paintRectangle);

            //Draw line
            canvas.drawLine(marginLeft, pi+marginTop, marginLeft + bs, pi+marginTop, paintLine);
            //Draw line tops
            canvas.drawLine(marginLeft, pi+marginTop - lineTopSize/2, marginLeft, pi+marginTop + lineTopSize/2, paintLine);
            canvas.drawLine(marginLeft +bs, pi+marginTop - lineTopSize/2, marginLeft+bs, pi+marginTop + lineTopSize/2, paintLine);
            //Draw line midle
            canvas.drawLine(marginLeft + bs/2, pi+marginTop - lineTopSize/3, marginLeft + bs/2, pi+marginTop + lineTopSize/3, paintLine);
            //Draw line quarters
            canvas.drawLine(marginLeft + bs/4, pi+marginTop - lineTopSize/4, marginLeft + bs/4, pi+marginTop + lineTopSize/4, paintLine);
            canvas.drawLine(marginLeft + 3*bs/4, pi+marginTop - lineTopSize/4, marginLeft + 3*bs/4, pi+marginTop + lineTopSize/4, paintLine);

            //Draw text
            canvas.drawText(text, marginLeft +bs, pi+marginTop+paintText.getFontSpacing(), paintText);
        }
    }
}