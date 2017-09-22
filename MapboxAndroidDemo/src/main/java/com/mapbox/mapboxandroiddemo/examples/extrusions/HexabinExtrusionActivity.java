package com.mapbox.mapboxandroiddemo.examples.extrusions;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxandroiddemo.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.functions.Function;
import com.mapbox.mapboxsdk.style.functions.stops.IntervalStops;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.commons.geojson.FeatureCollection;

import java.io.IOException;
import java.io.InputStream;

import static com.mapbox.mapboxsdk.style.functions.Function.zoom;
import static com.mapbox.mapboxsdk.style.functions.stops.Stop.stop;
import static com.mapbox.mapboxsdk.style.functions.stops.Stops.exponential;
import static com.mapbox.mapboxsdk.style.functions.stops.Stops.interval;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleBlur;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class HexabinExtrusionActivity extends AppCompatActivity /*implements
  MapView.OnMapChangedListener, MapboxMap.OnMapLongClickListener */ {

  private MapView mapView;
  private MapboxMap map;
  private int maxColor;
  private String[] colorStops = {"#151515", "#222", "#ffc300", "#ff8d19", "#ff5733", "#ff2e00"};
  private int heightStop = 5000;
  private String colorActive = "#3cc";
  private String[] typeList = {"total", "noise", "establishment", "poisoning", "drinking", "smoking", "others"};
  // active filter for each of the filter session
  private String activeCamera = "hexbin";
  private String activeType = "total";
  // result data field of camera, type, method combined
  private String activeDds = "totalDensity";
  private CameraPosition previousCamera;
  private FeatureCollection empty;
  private FeatureCollection gridActive;
  private FeatureCollection pointActive;



  /*private maxColor =max[activeDds];
  private maxHeight =max["totalDensity"];
*/
  // for DDS threshholds, [total, density]
  /*private max =

  {
    "businesses":46,
    "total":283,
    "noise":278,
    "establishment":60,
    "poisoning":15,
    "drinking":8,
    "smoking":10,
    "others":9,
    "totalDensity":141.5,
    "noiseDensity":139,
    "establishmentDensity":20,
    "poisoningDensity":8,
    "drinkingDensity":5,
    "smokingDensity":10,
    "othersDensity":1.3,
  }*/


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Mapbox access token is configured here. This needs to be called either in your application
    // object or in the same activity which contains the mapview.
    Mapbox.getInstance(this, getString(R.string.access_token));

    setContentView(R.layout.activity_hexabin_extrusion);

    mapView = (MapView) findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);

    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(MapboxMap mapboxMap) {
        /* addGrids3dLayer();
        setUpActiveGrid();*/

        HexabinExtrusionActivity.this.map = mapboxMap;

        mapboxMap.setLatLngBoundsForCameraTarget(new LatLngBounds.Builder()
          .include(new LatLng(40.609614478818855, -74.09692544272578))
          .include(new LatLng(40.846999364699144, -73.77487016324935))
          .build());

        setUpComplaints();
        setUpBusinesses();
        addGrids3dLayer();
        setUpActiveGrid();

        setUpGridsCountLayer();
        setUpPointsActiveLayer();
      }
    });
  }

  /*  @Override
    public void onMapChanged(int change) {
      if (!activeCamera.equals("inspector")) {
        activeCamera = map.getCameraPosition().zoom > 14 ? "dotted" : "hexbin";
        setLayers();
      }

    }*/
  /*@Override
  public void onMapLongClick(@NonNull LatLng point) {
    LatLng coordinates = new LatLng(point.getLatitude(), point.getLongitude());
    String html = "";
    Feature[] queryComplaints;
    if (activeCamera.equals("hexbin")) {
      Feature[] query = map.queryRenderedFeatures(point., "grids-3d");
      if (query.) {
        html = query[0].properties.total + " complaints here with " + query[0].properties.businesses + " restaurants/cafes/bars. ";
        html = activeType == = "total" ? html : html + query[0].properties[activeType] + " of them are about " + activeType + ". ";
        html += "Click to see the incidents.";

        gridActive.features = query[0]];
        map.getSource("grid-active").setData(gridActive);
      } else {
        map.getSource("grid-active").setData(gridActive);
      }
      // else: "dotted" or "inspector"
    } else {
      queryComplaints = map.queryRenderedFeatures(point., "points-complaints");

    }
    if (queryComplaints.length()) {
      html += "<h2>" + queryComplaints.length + " complaint(s):</h2>";

      // show top 5 and hide the others
      int max = 3;
      int length = queryComplaints.length() <= max ? queryComplaints.length : max;
      for (int index = 0; index < length; index++) {
        String complaint = "<p>" + queryComplaints[0].getStringProperty("Complaint Type")
          + " : " + queryComplaints[0].getStringProperty("Descriptor") + "</p>";
        html += complaint;
      }
      ;
      if (queryComplaints.length > max) {
        html += "<p>...</p>";
      }
      pointActive.getFeatures() = queryComplaints;
      map.getSource("point-active").setData(pointActive);
    } else {
      map.getSource("point-active").setData(empty);
    }

    if (html.equals("")) {
      popup.remove();
      $(".mapboxgl-canvas-container").css("cursor", "-webkit-grab");
    } else {
      $(".mapboxgl-canvas-container").css("cursor", "none");
      popup.setLngLat(coordinates)
        .setHTML(html)
        .addTo(map);
    }
  }*/

  private void setLayers() {
    if (activeCamera.equals("hexbin")) {
      map.getLayer("points-complaints").setProperties(circleOpacity(0f));
      map.getLayer("points-businesses").setProperties(circleOpacity(0f));
      map.getLayer("grids-3d").setProperties(fillExtrusionOpacity(0.6f));
      map.getLayer("grids-active").setProperties(fillExtrusionOpacity(0.6f));
      map.getLayer("grids-count").setProperties(textOpacity(0f));
      map.getSource("point-active");
    } else {
      if (activeCamera.equals("dotted")) {
        map.getLayer("points-complaints").setProperties(circleOpacity(0.3f));
        map.getLayer("points-businesses").setProperties(circleOpacity(0.2f));
        map.getLayer("grids-3d").setProperties(fillExtrusionOpacity(0f));
        map.getLayer("grids-active").setProperties(fillExtrusionOpacity(0f));
        map.getLayer("grids-count").setProperties(textOpacity(0.8f));
        map.getSource("grid-active");

      } else {
        if (activeCamera.equals("inspector")) {
          map.getLayer("points-complaints").setProperties(circleOpacity(0.3f));
          map.getLayer("points-businesses").setProperties(circleOpacity(0.2f));
          map.getLayer("grids-3d").setProperties(fillExtrusionOpacity(0.0f));
          map.getLayer("grids-active").setProperties(fillExtrusionOpacity(0.2f));
          map.getLayer("grids-active").setProperties(fillExtrusionHeight(0f));
          map.getLayer("grids-count").setProperties(textOpacity(0.8f));
        }
      }
    }
  }

  private void addGrids3dLayer() {
    GeoJsonSource gridSource = new GeoJsonSource("grids", loadJsonFromAsset("grids.geojson"));
    map.addSource(gridSource);

    /*FillExtrusionLayer fillExtrusionLayer3dGrid = new FillExtrusionLayer("grids-3d", "grids");
    fillExtrusionLayer3dGrid.withProperties(
      fillExtrusionColor(Function.property("population",
        IntervalStops.interval(
          stop(0, fillColor(Color.parseColor(colorStops[1])),
            stop(maxColor * .2, fillColor(Color.parseColor(colorStops[1])),
              stop(maxColor * .5, fillColor(Color.parseColor(colorStops[2])),
                stop(maxColor * .8, fillColor(Color.parseColor(colorStops[3])),
                  stop(maxColor * .2, fillColor(Color.parseColor(colorStops[4]))),
                    stop(maxColor, fillColor(Color.parseColor(colorStops[5]))),
                    fillExtrusionHeight(Function.property("activeDds", IntervalStops.interval(
                      stop(0, fillExtrusionHeight(0f),
                        fillExtrusionOpacity(0.9f))))))))))))));
    */

    /*FillExtrusionLayer fillExtrusionLayer3dGrid = new FillExtrusionLayer("grids-3d", "grids");
    fillExtrusionLayer3dGrid.setProperties(
      fillExtrusionColor(Function.property("population",
        IntervalStops.interval(
          stop(0, fillColor(Color.parseColor(colorStops[1])),
            stop(maxColor * .2, fillColor(Color.parseColor(colorStops[1]),
              stop(maxColor * .5, fillColor(Color.parseColor(colorStops[2]),
                stop(maxColor * .8, fillColor(Color.parseColor(colorStops[3]),
                  stop(maxColor * .2, fillColor(Color.parseColor(colorStops[4])),
                    stop(maxColor, fillColor(Color.parseColor(colorStops[5])))),
                  fillExtrusionHeight(Function.property("activeDds", IntervalStops.interval(
                    stop(0, fillExtrusionHeight(0f),
                      fillExtrusionOpacity(0.9f))))))));


    map.addLayerAbove(fillExtrusionLayer3dGrid, "admin-2-boundaries-dispute");*/

  }

  private void setUpActiveGrid() {
    GeoJsonSource gridActiveSource = new GeoJsonSource("grid-active", loadJsonFromAsset("grids.geojson"));
    map.addSource(gridActiveSource);

    FillExtrusionLayer fillExtrusionLayerActiveGridLayer = new FillExtrusionLayer("grid-active", "grid-active");
    fillExtrusionLayerActiveGridLayer.withProperties(
      fillExtrusionColor(colorActive),
      fillExtrusionHeight(Function.property("activeDds", IntervalStops.interval(
        stop(0, fillExtrusionHeight(0f))))),
      fillExtrusionOpacity(0.6f));

    map.addLayerAbove(fillExtrusionLayerActiveGridLayer, "admin-2-boundaries-dispute");
  }

  private void setUpComplaints() {

    GeoJsonSource complaintSource = new GeoJsonSource("points-complaints",
      loadJsonFromAsset("data_complaints.geojson"));
    map.addSource(complaintSource);

    CircleLayer complaintCirclesLayer = new CircleLayer("points-complaints", "points-complaints");
    complaintCirclesLayer.withProperties(
      circleRadius(
        zoom(
          interval(
            stop(12, circleRadius(1f)),
            stop(15, circleRadius(5f))
          )
        )
      ),
      circleColor(Color.parseColor("#bfff0f"))
    );
    map.addLayerBelow(complaintCirclesLayer, "admin-2-boundaries-dispute");
  }

  private void setUpBusinesses() {

    GeoJsonSource businessSource = new GeoJsonSource("points-businesses", loadJsonFromAsset("data_businesses.geojson"));
    map.addSource(businessSource);

    CircleLayer businessCircleLayer = new CircleLayer("points-businesses", "points-businesses");
    businessCircleLayer.withProperties(
      circleRadius(
        zoom(
          exponential(
            stop(12, circleRadius(3f)),
            stop(15, circleRadius(8f))
          )
        )
      ),
      circleColor(colorStops[5])
    );

    map.addLayerBelow(businessCircleLayer, "admin-2-boundaries-dispute");
  }

  private void setUpGridsCountLayer() {
    SymbolLayer gridsCountLayer = new SymbolLayer("grids-count", "grids");
    gridsCountLayer.withProperties(
      textOpacity(0f),
      textSize(14f),
      textField("{" + activeDds + "}"),
      textColor(colorStops[2])
    );

    map.addLayer(gridsCountLayer);
  }

  private void setUpPointsActiveLayer() {
    GeoJsonSource pointActiveSource = new GeoJsonSource("point-active", "pointActive");
    map.addSource(pointActiveSource);

    CircleLayer activePointCircleLayer = new CircleLayer("point-active", "point-active");
    activePointCircleLayer.withProperties(
      circleRadius(15f),
      circleColor(colorStops[2]),
      circleOpacity(.3f),
      circleBlur(1f)
    );

    map.addLayerAbove(activePointCircleLayer, "points-businesses");
  }

  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  /*
    private void getCamera() {
      // if pitch==0, don't update Camera
      if (map.getCameraPosition().tilt == 0) {
        previousCamera.target = map.getCameraPosition().target;
        previousCamera.zoom = map.getCameraPosition().zoom;
        previousCamera.tilt = map.getCameraPosition().tilt;
        previousCamera.bearing = map.getCameraPosition().bearing;
      }
    }
  */

  private String loadJsonFromAsset(String filename) {
    // Using this method to load in GeoJSON files from the assets folder.

    try {
      InputStream is = getAssets().open(filename);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      return new String(buffer, "UTF-8");

    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }


}
