package highschool.pranav.floodmapnavigator;

import android.*;
import android.Manifest;
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

import com.directions.route.Route;
import com.directions.route.RouteException;
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
import com.google.android.gms.maps.model.PolygonOptions;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        LocationListener, GoogleApiClient.OnConnectionFailedListener, DownloadWebpageTask.FloodAssyncResponse, RoutingListener {

    private ArrayList<Flood> worldFlood;
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
                LatLng userLoc = new LatLng(loc.getLatitude(), loc.getLongitude());

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
                LatLng userLoc = new LatLng(loc.getLatitude(), loc.getLongitude());

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
        for(i = 0; i<worldFlood.size(); i++){
            Flood floodIterate = worldFlood.get(i);
            Location min = floodIterate.getBoundBoxMin();
            Location max = floodIterate.getBoundBoxMax();
            int alertLevel = floodIterate.getAlertLevel();

            LatLng minLoc = new LatLng(min.getLatitude(), min.getLongitude());
            LatLng maxLoc = new LatLng(max.getLatitude(), max.getLongitude());

            BitmapDescriptor icon;

            switch(alertLevel){
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
            mMap.addPolygon(rectOptions);


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
                mMap.addMarker( new MarkerOptions().position(latLng).title("POINTERS").icon(icon2));

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
        //
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
    //Implrmrnt code in here
        //line 380 code to be here
        //create a new method after porcessing flood
    }

    @Override
    public void onRoutingCancelled() {

    }
}

