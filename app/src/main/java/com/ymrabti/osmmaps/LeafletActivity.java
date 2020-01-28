package com.ymrabti.osmmaps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebView;

public class LeafletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaflet);
        ActionBar actionBar= getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Leaflet Maps");
        }
        WebView myWebView = new WebView(this);
        setContentView(myWebView);
        //myWebView.loadUrl("http://www.igli5.com");
        String unencodedHtml = "<html>\n" +
                "<head>\n" +
                "  <title>A Leaflet map!</title>\n" +
                "  <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.css\"/>\n" +
                "  <script src=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.js\"></script>\n" +
                "  <style>\n" +
                "    #map{ height: 100% }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "  <div id=\"map\"></div>\n" +
                "\n" +
                "  <script>\n" +
                "\n" +
                "  // initialize the map\n" +
                "  var map = L.map('map').setView([42.35, -71.08], 13);\n" +
                "\n" +
                "  // load a tile layer\n" +
                "  L.tileLayer('http://tiles.mapc.org/basemap/{z}/{x}/{y}.png',\n" +
                "    {\n" +
                "      attribution: 'Tiles by <a href=\"http://mapc.org\">MAPC</a>, Data by <a href=\"http://mass.gov/mgis\">MassGIS</a>',\n" +
                "      maxZoom: 17,\n" +
                "      minZoom: 1\n" +
                "    }).addTo(map);\n" +
                "\n" +
                "  </script>\n" +
                "</body>\n" +
                "</html>" ;
        String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(),
                Base64.NO_PADDING);
        myWebView.loadData(encodedHtml, "text/html", "base64");
    }
}
