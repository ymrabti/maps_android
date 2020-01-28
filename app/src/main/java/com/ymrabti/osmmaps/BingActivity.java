package com.ymrabti.osmmaps;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;
import com.microsoft.maps.MapProjection;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapStyleSheet;
import com.microsoft.maps.MapStyleSheets;
import com.microsoft.maps.MapTappedEventArgs;
import com.microsoft.maps.MapView;
import com.ymrabti.osmmaps.examles.LocalSearch;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BingActivity extends AppCompatActivity {
    private MapElementLayer mPinLayer;
    private MapView mMapView;


    private static final MapStyleSheet[] MAP_STYLES = {
            MapStyleSheets.roadLight(),
            MapStyleSheets.roadDark(),
            MapStyleSheets.roadCanvasLight(),
            MapStyleSheets.aerial(),
            MapStyleSheets.aerialWithOverlay(),
            MapStyleSheets.roadHighContrastLight(),
            MapStyleSheets.roadHighContrastDark(),
    };
    private static final int POSITION_CUSTOM = MAP_STYLES.length;

    private static final Geopoint LOCATION_LAKE_EHTP =
            new Geopoint(33.547595,-7.650056);

    private MapImage mPinImage;
    private View mDemoMenu;
    private Spinner mStyleSpinner;
    private int mStyleSpinnerPosition;
    private AppCompatButton mButtonPoiTap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bing);
        ActionBar actionBar= getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Bing Maps");
        }


        // Enable translucent status bar if supported by Android version.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        mMapView = new MapView(this, MapRenderMode.VECTOR);
        mMapView.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
        ((FrameLayout)findViewById(R.id.map_view_bing)).addView(mMapView);
        mMapView.onCreate(savedInstanceState);
        mPinLayer = new MapElementLayer();
        mMapView.getLayers().add(mPinLayer);
        mPinImage = getPinImage();
        mMapView.setScene(
                MapScene.createFromLocationAndZoomLevel(LOCATION_LAKE_EHTP, 10),
                MapAnimationKind.NONE);


        String title =  "marker micro soft";
        Bitmap pinBitmap = ((BitmapDrawable) Objects.requireNonNull(getDrawable(R.drawable.circle))).getBitmap() ;
        MapIcon pushpin = new MapIcon();
        pushpin.setLocation(LOCATION_LAKE_EHTP);
        pushpin.setTitle(title);
        pushpin.setImage(new MapImage(pinBitmap));
        mPinLayer.getElements().add(pushpin);
        mMapView.setMapStyleSheet(MapStyleSheets.roadDark());
        mMapView.setMapProjection(MapProjection.GLOBE);

        setupDemoMenu();
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

    private void addPin(Geopoint location, String title) {
        MapIcon pushpin = new MapIcon();
        pushpin.setLocation(location);
        pushpin.setTitle(title);
        pushpin.setImage(mPinImage);
        pushpin.setNormalizedAnchorPoint(new PointF(0.5f, 1f));
        mPinLayer.getElements().add(pushpin);
    }

    private void clearPins() {
        mPinLayer.getElements().clear();
    }

    private void showCustomStyleDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Paste style JSON here");
        input.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        new AlertDialog.Builder(this)
                .setView(input)
                .setPositiveButton("Set", (DialogInterface dialog, int which) -> {
                    MapStyleSheet style = MapStyleSheet.fromJson(input.getText().toString());
                    if (style != null) {
                        mMapView.setMapStyleSheet(style);
                    } else {
                        mStyleSpinner.setSelection(mStyleSpinnerPosition);
                        Toast.makeText(BingActivity.this,"Custom style JSON is invalid", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> dialog.cancel())
                .setOnCancelListener((DialogInterface dialog) ->
                        mStyleSpinner.setSelection(mStyleSpinnerPosition))
                .show();
    }

    private void showPoiSearchDialog() {
        EditText input = new EditText(this);
        input.setHint("Search query");
        input.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        new AlertDialog.Builder(this)
                .setView(input)
                .setPositiveButton("Search", (DialogInterface dialog, int which) ->
                        LocalSearch.sendRequest(this, input.getText().toString(), mMapView.getBounds(), new LocalSearch.Callback() {
                            @Override
                            public void onSuccess(List<LocalSearch.Poi> results) {
                                clearPins();
                                for (LocalSearch.Poi result : results) {
                                    addPin(result.location, result.name);
                                }
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(BingActivity.this,"No search results found", Toast.LENGTH_LONG).show();
                            }
                        }))
                .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> dialog.cancel())
                .show();
    }

    private MapImage getPinImage() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pin, null);

        int width = 0;
        if (drawable != null) {
            width = drawable.getIntrinsicWidth();
        }
        int height = 0;
        if (drawable != null) {
            height = drawable.getIntrinsicHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        if (drawable != null) {
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        }
        if (drawable != null) {
            drawable.draw(canvas);
        }

        return new MapImage(bitmap);
    }


    private void setupDemoMenu() {
        mDemoMenu = findViewById(R.id.menu_demo);

        FloatingActionButton buttonDemo = findViewById(R.id.button_demo);
        buttonDemo.setCompatElevation(0);
        buttonDemo.setOnClickListener((View v) -> {
            boolean wasVisible = mDemoMenu.getVisibility() == View.VISIBLE;
            mDemoMenu.setVisibility(wasVisible ? View.GONE : View.VISIBLE);
        });
        mStyleSpinner = findViewById(R.id.spinner_style);

        mStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == mStyleSpinnerPosition) {
                    return;
                }
                mStyleSpinnerPosition = position;
                if (position == POSITION_CUSTOM) {
                    showCustomStyleDialog();
                    return;
                }
                mMapView.setMapStyleSheet(MAP_STYLES[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {  /*required*/  }
        });
        findViewById(R.id.button_style_custom).setOnClickListener((View v) ->
                mStyleSpinner.setSelection(POSITION_CUSTOM));

        ((SwitchCompat)findViewById(R.id.switch_projection)).setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isChecked) ->
                        mMapView.setMapProjection(isChecked ? MapProjection.GLOBE : MapProjection.WEB_MERCATOR));
        mButtonPoiTap = findViewById(R.id.button_poi_tap);
        mButtonPoiTap.setTag(false);
        mButtonPoiTap.setOnClickListener((View v) -> {
            boolean wasActive = (boolean) v.getTag();
            v.setBackground(wasActive
                    ? ResourcesCompat.getDrawable(v.getResources(), R.drawable.button_default, null)
                    : ResourcesCompat.getDrawable(v.getResources(), R.drawable.button_active, null));
            v.setTag(!wasActive);
        });
        mMapView.addOnMapTappedListener((MapTappedEventArgs e) -> {
            if ((boolean) mButtonPoiTap.getTag()) {
                Geopoint location = mMapView.getLocationFromOffset(e.position);
                if (location != null) {
                    addPin(location, "");
                }
            }
            return false;
        });

        findViewById(R.id.button_poi_search).setOnClickListener((View v) -> showPoiSearchDialog());
        findViewById(R.id.button_poi_clear).setOnClickListener((View v) -> clearPins());
    }

}
