package highschool.pranav.floodmapnavigator;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        LocationListener, GoogleApiClient.OnConnectionFailedListener, DownloadWebpageTask.FloodAssyncResponse, RoutingListener {
    /**
     * 47.34, -124.92
     * 33.57, -80.10000000000001
     */
    final double floodLat = 29.43;
    final double floodLong = -107.64;//-106.44003240;
    private LatLng userFloodLocation = new LatLng(floodLat, floodLong);
    private String googleElevationAPIKey = "AIzaSyAaVTprHAxbZ3Q5GaSwA4A1r7V0nU4Vx28";

    private ArrayList<Flood> worldFlood;

    private ProgressDialog progressDialog;

    private GoogleMap mMap;
    //https://github.com/jd-alexander/Google-Directions-Android/blob/master/sample/src/main/java/com/directions/sample/MainActivity.java
    //Create an Interface Routing Listener Interface
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Instantiate the cache
        cache = new DiskBasedCache(getApplicationContext().getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.5
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mRequestQueue.start();
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction1());

        DownloadWebpageTask downloadWebpageTask = new DownloadWebpageTask();
        downloadWebpageTask.responseDelegate = this;
        downloadWebpageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{});
    }

    public void onConnected(Bundle connectionHint) {
        if (client.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(client, LocationRequest.create(), (LocationListener) this);
            Location loc = LocationServices.FusedLocationApi.getLastLocation(client);

            if (loc != null) {
                //LatLng userLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
                LatLng userLoc = userFloodLocation;
                mMap.addMarker(new MarkerOptions().position(userLoc).title("YOU ARE HERE"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(1090));
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(userLoc));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    // Step 3: Show major highways
    // Step 4: Show major natural landmarks
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(3);
        mMap.setMyLocationEnabled(true);
        //New code added for permission issue
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getFocusedBuilding();
        mMap.setIndoorEnabled(true);


        if (client.isConnected()) {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(client);

            if (loc != null) {
                //LatLng userLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
                LatLng userLoc = userFloodLocation;
                mMap.addMarker(new MarkerOptions().position(userLoc).title("YOUR LOCATION"));
                mMap.addCircle(new CircleOptions().visible(true).center(userLoc).radius(100).strokeColor(0x001234ff).strokeWidth(10));

                mMap.addPolygon(new PolygonOptions().strokeColor(0x000000).strokeWidth(10));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));

            }
        }
        // This is called when the user has not yet granted permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction0() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction1() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    //TODO
    //tell user if not connected
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            return true;
        }
        return false;
    }


    /**
     * This is for actual plotting of data being called from OnPostExecute of DownloadWebPage Task
     *
     * @param floods
     */
    @Override
    public void processFloodData(ArrayList<Flood> floods) {
        this.worldFlood = floods;
        int i;
        Flood userFlood = null;
        for (i = 0; i < worldFlood.size(); i++) {
            Flood floodIterate = worldFlood.get(i);

            Location min = floodIterate.getBoundBoxMin();
            Location max = floodIterate.getBoundBoxMax();

            int alertLevel = floodIterate.getAlertLevel();

            LatLng minLoc = new LatLng(min.getLatitude(), min.getLongitude());
            LatLng maxLoc = new LatLng(max.getLatitude(), max.getLongitude());
            //if (userFloodLocation.latitude > minLoc.latitude && userFloodLocation.latitude < maxLoc.latitude)
            BitmapDescriptor icon;

            switch (alertLevel) {
                case 2:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    break;
                case 3:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                    break;
                case 4:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                default:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            }
            mMap.addMarker(new MarkerOptions()
                    .position(minLoc)
                    .icon(icon)
                    .title("MIN FLOOD" + alertLevel));
            mMap.addMarker(new MarkerOptions()
                    .position(maxLoc)
                    .icon(icon)
                    .title("MAX FLOOD" + alertLevel));
            /**
             * Plotting 4 points to highlight flood path based on max and min coordinates
             * This is done with Polygon
             */
            ArrayList<LatLng> points = floodIterate.getLatLngArrayList();
//          Collections.reverse(points);

            PolygonOptions rectOptions = new PolygonOptions()
                    .addAll(points).fillColor(6987504);
            rectOptions.strokeColor(Color.CYAN).strokeWidth(5);

            Polygon polygon = mMap.addPolygon(rectOptions);
            boolean containsLoc = PolyUtil.containsLocation(userFloodLocation, points, true);
            if (containsLoc) {
                userFlood = floodIterate;
            }


            /**
             * To Do is to either plot Tiles with Points Data or draw a polygon/layout with max and min lat and long coordinates
             */
            /**
             * Once the flood Data is mapped we need to suggest user to better elevation in MAP using the coordinates for alternate paths
             */
            //Loop to iterate latLangArrayList for tiles mapping
            for (LatLng latLng : floodIterate.getLatLngArrayList()) {
                //This is place holder for adding tiles as Polygon
                BitmapDescriptor icon2 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                // Log.v("MY POINTERS " + );
                //Log.v("tag", "MY POINTER Lat " + latLng.latitude);
                //Log.v("tag", "MY POINTER Lng " + latLng.longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title("POINTERS ").icon(icon2));
            }


            /**
             * Elevation API integration for safe route
             */
            /**
             *
             * Restrict data by country the user is in
             *
             */
            /**
             * Add timer for auto refresh of flood data
             */
            //Call the method or logic to calculate the route and map
            //line # 354
            // https://github.com/jd-alexander/Google-Directions-Android/blob/master/sample/src/main/java/com/directions/sample/MainActivity.java

        }
//        Log.v("tag", "NEW User Lat: " + userFlood.latLngArrayList.get(0).latitude);
//        Log.v("tag", "NEW User Lng: " + userFlood.latLngArrayList.get(0).longitude);
//        Log.v("tag", "NEW User Lat: " + userFlood.latLngArrayList.get(1).latitude);
//        Log.v("tag", "NEW User Lng: " + userFlood.latLngArrayList.get(1).longitude);
//        Log.v("tag", "User Flood Size: " + userFlood.latLngArrayList.size());

       if (userFlood.getLatLngArrayList().size() >0) {//CHANGED FROM userFlood!=null as it was not going inside the loop
            //if user flood is null then the user is not inside of a flood right now
            //TODO:tell the user something if they are not inside of a flood like safe place
            url = url + "location=" + userFloodLocation.latitude + "," + userFloodLocation.longitude + "&radius=50000&key=" + PLACES_KEY;
           Log.v("URL", "URL FORMED: " + url);
           mRequestQueue.add(mapPlacesRequest);
        }

        //By default added end location
        //instead we need to make web services call to Google Elevation API[This accepts a point as the parameter,
        //so we will have to create an array of points within the flood, and put the call in a for-loop,
        //if-statement combo that will iterate through the array and get each value and pass it to the API, then check based on our parameters we have with the if-statement]
        //Based on the previous steps,
        //we can add all the eligible points to and array and plot these, then use one of the available algorithms{breath first search, depth first search, etc.]
        //https://maps.googleapis.com/maps/api/elevation/json?locations=35.56360612905,-112.710044076&key=AIzaSyAaVTprHAxbZ3Q5GaSwA4A1r7V0nU4Vx28 -> Current Elevation for flood data
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=35.56360612905,-112.710044076&radius=150000&types=food&name=cruise&key=AIzaSyCOtzm-A5wVw1ixYKs0uWSd94NITbJ-93c
    }

    //route calculation
    //Need to get latLngStart and latLngEnd
    public void route(ArrayList<LatLng> startEndLocs) {

        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(startEndLocs)
                .build();
        routing.execute();

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.d("check", "ROUTING FALIURE");
        progressDialog.hide();
        progressDialog = ProgressDialog.show(this, "FAILED TO ROUTE", e.getLocalizedMessage(), false, true);
    }

    @Override
    public void onRoutingStart() {
        Log.d("check", "ROUTING START");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int shortestPathIndex) {
        Log.d("check", "ROUTING SUCESS");
        progressDialog.hide();
        //Implement code in here
        //line 380 code to be here
        //create a new method after porcessing flood
        Route route = routes.get(shortestPathIndex);
        List<LatLng> points = route.getPoints();
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
        }
        PolylineOptions rectOptions = new PolylineOptions()
                .addAll(points);
        rectOptions.color(Color.BLACK).width(5);
        mMap.addPolyline(rectOptions);
        Log.d("check", "ROUTING SUCESS TWO");
    }

    @Override
    public void onRoutingCancelled() {

    }

    //This is going to be the Places API JSON request.
    final String PLACES_KEY = "AIzaSyCvH_MAUyZw54O2JeSBdlR0JCPLXXAf9VU";//"AIzaSyD-y_wzRSKlVnygBaqogacfFjS8V7y5cog";
    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    String rBody = null;
    // Instantiate the cache
    Cache cache; // 1MB cap

    // Set up the network to use HttpURLConnection as the HTTP client.
    Network network;

    // Instantiate the RequestQueue with the cache and network.
    RequestQueue mRequestQueue;
    Context con = this;
    JSONObject jsonReqObject = null;
    //This is not getting executed ????


    JsonObjectRequest mapPlacesRequest = new JsonObjectRequest
            (Request.Method.GET, url,jsonReqObject,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.v("URL", "URL PASSED: " + url);
                        JSONArray placeJSON = response.getJSONArray("results");
                        Log.v("tag", "JSON Results " + response.toString());
                        JSONObject place = (JSONObject) placeJSON.get(0);
                        JSONObject geometry = place.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");
                        // currently no using actual user location for testing
                        if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location loc = LocationServices.FusedLocationApi.getLastLocation(client);
                        // LatLng userLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
                        LatLng userLoc = userFloodLocation;

                        ArrayList<LatLng> floodStartEndLocs = new ArrayList();
                        floodStartEndLocs.add(userLoc);
                        LatLng endLoc = new LatLng(lat, lng);
                        floodStartEndLocs.add(endLoc);
                        Log.d("check", "ROUTING STARTED");
                        Log.v("tag", "ROUTING Lat Values " + lat);
                        Log.v("tag", "ROUTING Lng Values " + lng);
                        route(floodStartEndLocs);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.v("tag", "Error Message ROUTING" + error.getMessage());
                    Log.v("tag", "Error String " + error.toString());

                }

            });

}

