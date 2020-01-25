package com.ymrabti.osmmaps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

public class ArcGISscesneActivity extends AppCompatActivity {
    private SceneView mSceneView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_gisscesne);
        ActionBar actionBar= getSupportActionBar();actionBar.setTitle("Arc Scene");
        mSceneView = findViewById(R.id.sceneView);
        ArcGISRuntimeEnvironment.setLicense(getResources().getString(R.string.arcgis_license_key));
        setupScene1();
    }

    private void setupScene() {
        if (mSceneView != null) {
            Basemap.Type basemapType = Basemap.Type.IMAGERY_WITH_LABELS;
            ArcGISScene scene = new ArcGISScene(basemapType);
            mSceneView.setScene(scene);

            double latitude = 33.547595;
            double longitude = -7.650056;
            double altitude = 25000.0;
            double heading = 0.1;
            double pitch = 45.0;
            double roll = 0.0;

            Camera camera = new Camera(latitude, longitude, altitude, heading, pitch, roll);
            mSceneView.setViewpointCamera(camera);
            addTrailheadsLayer();
            setElevationSource(scene);
        }
    }
    private void setupScene1() {
        if (mSceneView != null)
        {
            String itemID = "579f97b2f3b94d4a8e48a5f140a6639b";
            Portal portal = new Portal("https://www.arcgis.com");
            PortalItem portalItem = new PortalItem(portal, itemID);
            ArcGISScene scene = new ArcGISScene(portalItem);
            mSceneView.setScene(scene);
            double latitude = 32.547595;
            double longitude = -7.650056;
            double altitude = 25000.0;
            double heading = 0.1;
            double pitch = 45.0;
            double roll = 0.0;

            Camera camera = new Camera(latitude, longitude, altitude, heading, pitch, roll);
            mSceneView.setViewpointCamera(camera);
            addTrailheadsLayer();
        }
    }
    private void addTrailheadsLayer() {
        String url = "https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trails/FeatureServer/0";
        final ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        mSceneView.getScene().getOperationalLayers().add(featureLayer);
    }
    private void setElevationSource(ArcGISScene scene) {
        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(
                "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
        scene.getBaseSurface().getElevationSources().add(elevationSource);
    }
}
