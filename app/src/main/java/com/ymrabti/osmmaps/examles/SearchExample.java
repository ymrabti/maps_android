package com.ymrabti.osmmaps.examles;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.CustomMetadataValue;
import com.here.sdk.core.GeoBox;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.TextFormat;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.GestureState;
import com.here.sdk.mapviewlite.Camera;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.search.AutosuggestCallback;
import com.here.sdk.search.AutosuggestEngine;
import com.here.sdk.search.AutosuggestOptions;
import com.here.sdk.search.AutosuggestResult;
import com.here.sdk.search.AutosuggestResultType;
import com.here.sdk.search.GeocodingEngine;
import com.here.sdk.search.GeocodingOptions;
import com.here.sdk.search.GeocodingResult;
import com.here.sdk.search.ReverseGeocodingEngine;
import com.here.sdk.search.ReverseGeocodingOptions;
import com.here.sdk.search.SearchCategory;
import com.here.sdk.search.SearchEngine;
import com.here.sdk.search.SearchOptions;
import com.here.sdk.search.SearchResult;
import com.here.sdk.searchcommon.Address;
import com.ymrabti.osmmaps.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class SearchExample {

    private Context context;
    private MapViewLite mapView;
    private Camera camera;
    private List<MapMarker> mapMarkerList = new ArrayList<>();
    private SearchEngine searchEngine;
    private AutosuggestEngine autosuggestEngine;
    private GeocodingEngine geocodingEngine;
    private ReverseGeocodingEngine reverseGeocodingEngine;

    public SearchExample(Context context, MapViewLite mapView) {
        this.context = context;
        this.mapView = mapView;
        camera = mapView.getCamera();
        camera.setTarget(new GeoCoordinates(52.530932, 13.384915));
        camera.setZoomLevel(14);

        try {
            searchEngine = new SearchEngine();
        } catch (InstantiationErrorException e) {
            new RuntimeException("Initialization of SearchEngine failed: " + e.error.name());
        }

        try {
            autosuggestEngine = new AutosuggestEngine();
        } catch (InstantiationErrorException e) {
            new RuntimeException("Initialization of AutosuggestEngine failed: " + e.error.name());
        }

        try {
            geocodingEngine = new GeocodingEngine();
        } catch (InstantiationErrorException e) {
            new RuntimeException("Initialization of GeocodingEngine failed: " + e.error.name());
        }

        try {
            reverseGeocodingEngine = new ReverseGeocodingEngine();
        } catch (InstantiationErrorException e) {
            new RuntimeException("Initialization of ReverseGeocodingEngine failed: " + e.error.name());
        }

        setTapGestureHandler();
        setLongPressGestureHandler();

        Toast.makeText(context,"Long press on map to get the address for that position using reverse geocoding.", Toast.LENGTH_LONG).show();
    }

    public void onSearchButtonClicked() {
        // Search for "Pizza" and show the results on the map.
        searchExample();

        // Search for auto suggestions and log the results to the console.
        autoSuggestExample();
    }

    public void onGeocodeButtonClicked() {
        // Search for the location that belongs to an address and show it on the map.
        geocodeAnAddress();
    }

    private void searchExample() {
        String searchTerm = "Pizza";

        Toast.makeText(context,"Searching in viewport: " + searchTerm, Toast.LENGTH_LONG).show();
        searchInViewport(searchTerm);
    }

    private void geocodeAnAddress() {
        // Set map to expected location.
        camera.setTarget(new GeoCoordinates(52.530932, 13.384915));

        String streetName = "Invalidenstraße 116";

        Toast.makeText(context,"Finding locations for: " + streetName
               + ". Tap marker to see the coordinates.", Toast.LENGTH_LONG).show();

        geocodeAddressInViewport(streetName);
    }

    private void setTapGestureHandler() {
        mapView.getGestures().setTapListener(this::pickMapMarker);
    }

    private void setLongPressGestureHandler() {
        mapView.getGestures().setLongPressListener((gestureState, touchPoint) -> {
            if (gestureState == GestureState.BEGIN) {
                GeoCoordinates geoCoordinates = mapView.getCamera().viewToGeoCoordinates(touchPoint);
                addPoiMapMarker(geoCoordinates);
                getAddressForCoordinates(geoCoordinates);
            }
        });
    }

    private void getAddressForCoordinates(GeoCoordinates geoCoordinates) {
        // By default results are localized in EN_US.
        ReverseGeocodingOptions reverseGeocodingOptions = new ReverseGeocodingOptions(LanguageCode.EN_GB);

        reverseGeocodingEngine.searchAddress(
                geoCoordinates, reverseGeocodingOptions, (searchError, address) -> {
                    if (searchError != null) {
                        showDialog("Reverse geocoding", "Error: " + searchError.toString());
                        return;
                    }
                    showDialog("Reverse geocoded address:", address.addressText);
                });
    }

    private void pickMapMarker(final Point2D point2D) {
        float radiusInPixel = 2;
        mapView.pickMapItems(point2D, radiusInPixel, pickMapItemsResult -> {
            if (pickMapItemsResult == null) {
                return;
            }

            MapMarker topmostMapMarker = pickMapItemsResult.getTopmostMarker();
            if (topmostMapMarker == null) {
                return;
            }

            Metadata metadata = topmostMapMarker.getMetadata();
            if (metadata != null) {
                CustomMetadataValue customMetadataValue = metadata.getCustomValue("key_search_result");
                if (customMetadataValue != null) {
                    SearchResultMetadata searchResultMetadata = (SearchResultMetadata) customMetadataValue;
                    String title = searchResultMetadata.searchResult.title;
                    String vicinity = searchResultMetadata.searchResult.vicinity;
                    SearchCategory category = searchResultMetadata.searchResult.category;
                    showDialog("Picked Search Result",
                            title + ", " + vicinity + ". Category: " + category.localizedName);
                    return;
                }
            }

            showDialog("Picked Map Marker",
                    "Geographic coordinates: " +
                            topmostMapMarker.getCoordinates().latitude + ", " +
                            topmostMapMarker.getCoordinates().longitude);
        });
    }

    private void searchInViewport(String queryString) {
        clearMap();

        int maxSearchResults = 30;
        SearchOptions searchOptions = new SearchOptions(
                LanguageCode.EN_US,
                TextFormat.PLAIN,
                maxSearchResults);

        GeoBox viewportGeoBox = mapView.getCamera().getBoundingRect();
        searchEngine.search(viewportGeoBox, queryString, searchOptions, (searchError, list) -> {
            if (searchError != null) {
                showDialog("Search", "Error: " + searchError.toString());
                return;
            }

            if (list != null) {
                if (list.isEmpty()) {
                    showDialog("Search", "No results found");
                } else {
                    showDialog("Search", "Results: " + list.size());
                }
            }

            // Add new marker for each search result on map.
            if (list != null) {
                for (SearchResult searchResult : list) {
                    Metadata metadata = new Metadata();
                    metadata.setCustomValue("key_search_result", new SearchResultMetadata(searchResult));
                    addPoiMapMarker(searchResult.coordinates, metadata);
                }
            }
        });
    }

    private static class SearchResultMetadata implements CustomMetadataValue {

        public SearchResult searchResult;

        public SearchResultMetadata(SearchResult searchResult) {
            this.searchResult = searchResult;
        }

        @NonNull
        @Override
        public String getTag() {
            return "SearchResult Metadata";
        }
    }

    private final AutosuggestCallback autosuggestCallback = (searchError, list) -> {
        if (searchError != null) {
            Timber.d("Autosuggest Error: %s", searchError.name());
            return;
        }

        if (list != null) {
            if (list.isEmpty()) {
                Timber.d("Autosuggest: No results found");
            } else {
                Timber.d("Autosuggest results: %s", list.size());
            }
        }

        if (list != null) {
            for (AutosuggestResult autosuggestResult : list) {
                Timber.d("Autosuggest result: " + autosuggestResult.title +
                        "Highlighted: " + autosuggestResult.highlightedTitle);
            }
        }
    };

    private void autoSuggestExample() {
        GeoCoordinates centerGeoCoordinates = mapView.getCamera().getTarget();
        int maxSearchResults = 5;
        AutosuggestOptions autosuggestOptions = new AutosuggestOptions(
                LanguageCode.EN_US,
                TextFormat.PLAIN,
                maxSearchResults,
                new ArrayList<>(Collections.singletonList(
                        AutosuggestResultType.PLACE)));

        // Simulate a user typing a search term.
        autosuggestEngine.suggest(centerGeoCoordinates,
                "p",
                autosuggestOptions,
                autosuggestCallback);

        autosuggestEngine.suggest(centerGeoCoordinates,
                "pi",
                autosuggestOptions,
                autosuggestCallback);

        autosuggestEngine.suggest(centerGeoCoordinates,
                "piz",
                autosuggestOptions,
                autosuggestCallback);
    }

    private void geocodeAddressInViewport(String queryString) {
        clearMap();

        GeoBox geoBox = mapView.getCamera().getBoundingRect();
        long maxResultCount = 30;
        GeocodingOptions geocodingOptions = new GeocodingOptions(
                 LanguageCode.DE_DE, maxResultCount);

        geocodingEngine.searchLocations(geoBox, queryString, geocodingOptions, (searchError, list) -> {
            if (searchError != null) {
                showDialog("Geocoding", "Error: " + searchError.toString());
                return;
            }

            if (list != null && list.isEmpty()) {
                showDialog("Geocoding", "No geocoding results found.");
                return;
            }

            if (list != null) {
                for (GeocodingResult geocodingResult : list) {
                    GeoCoordinates geoCoordinates = geocodingResult.coordinates;
                    Address address = geocodingResult.address;
                    if (address != null) {
                        String locationDetails = address.addressText
                                + ". GeoCoordinates: " + geoCoordinates.latitude
                                + ", " + geoCoordinates.longitude;

                        Timber.d("GeocodingResult: %s", locationDetails);
                        addPoiMapMarker(geoCoordinates);
                    }
                }
            }

            showDialog("Geocoding result","Size: " + list.size());
        });
    }

    private void addPoiMapMarker(GeoCoordinates geoCoordinates) {
        MapMarker mapMarker = createPoiMapMarker(geoCoordinates);
        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void addPoiMapMarker(GeoCoordinates geoCoordinates, Metadata metadata) {
        MapMarker mapMarker = createPoiMapMarker(geoCoordinates);
        mapMarker.setMetadata(metadata);
        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private MapMarker createPoiMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.poi);
        MapMarker mapMarker = new MapMarker(geoCoordinates);
        MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
        mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5F, 1));
        mapMarker.addImage(mapImage, mapMarkerImageStyle);
        return mapMarker;
    }

    private void clearMap() {
        for (MapMarker mapMarker : mapMarkerList) {
            mapView.getMapScene().removeMapMarker(mapMarker);
        }
        mapMarkerList.clear();
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
