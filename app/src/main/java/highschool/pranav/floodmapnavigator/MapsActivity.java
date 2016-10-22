package highschool.pranav.floodmapnavigator;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import java.util.HashMap;
import java.util.StringTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private final String FLOOD_MERGE_URL = "http://www.gdacs.org/floodmerge/data.aspx";

    private ArrayList<Flood> worldFlood;
    private GoogleMap mMap;
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
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

                mMap.addPolygon(new PolygonOptions().strokeColor(0x000000).strokeWidth(5));
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

    private class DownloadWebpageTask extends AsyncTask<String, Void, ArrayList<Flood>> {

        ArrayList<Flood> floodArray = new ArrayList<Flood>();

        @Override
        protected void onPreExecute() {
            Log.v("tag", "On pre execute");
            super.onPreExecute();
        }

        @Override
        protected void onCancelled(ArrayList<Flood> floodArray) {
            super.onCancelled(floodArray);
        }

        @Override
        protected ArrayList<Flood> doInBackground(String... params) {
            Log.v("tag", "do in background");
            return queryFloodData();

        }

        @Override
        protected void onPostExecute(ArrayList<Flood> floodArray) {
            Log.v("tag", "Size is " + floodArray.size());
            for (Flood f: floodArray){
                //mMap.addMarker(new MarkerOptions().position(userLoc).title("YOUR LOCATION"));
                //googleMap.addMarker(new MarkerOptions().position(new LatLng( YOUR LATITUDE, -YOUR LOINGITUDE)).title("Marker"));
                //mMap.addMarker(new MarkerOptions().position(new LatLng( Double.parseDouble(f.getBoundBoxMaxLat()), -Double.parseDouble(f.getBoundBoxMaxLon()))).title("Flood Marker"));
            }
            super.onPostExecute(floodArray);
        }

        private ArrayList<Flood> queryFloodData() {
            //Log.v("tag", "query flood data");
            HttpURLConnection uConnect = null;
            URL fUrl = null;
            InputStream floodStream = null;
            BufferedReader floodReader = null;
            String floodFileData = "";
            try {
                fUrl = new URL(FLOOD_MERGE_URL);
            } catch (MalformedURLException e) {
                Log.v("tag", "malformed url " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            if (fUrl != null) {
                try {
                    uConnect = (HttpURLConnection) fUrl.openConnection();
                } catch (IOException e) {
                    Log.v("tag", "io exception in opening connection " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }

            if (uConnect != null) {
                try {
                    floodStream = uConnect.getInputStream();
                } catch (IOException e) {
                    Log.v("tag", "io exception in getting input stream " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
            if (floodStream != null) {
                floodReader = new BufferedReader(new InputStreamReader(floodStream));
                String floodString;
                try {
                    floodFileData = floodReader.readLine();
                    String[] floodData = floodFileData.split("<br>");
                    for (String s : floodData) {
                        if (!s.contains("AreasDataId")) {

                            Flood flood = setFloodDataValues(s);
                            floodArray.add(flood);
                            Log.v("tag", "reading flood data " + flood.getCountry());//This is for sample to see if parsing works
                        }
                    }


                } catch (IOException e) {
                    Log.v("tag", "io exception in reading flood data " + e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //Log.v("tag", "returning complete flood  file data: " + floodFileData);Not needed as parsing is successful
            return floodArray;
        }

        /**
         * @param floodLineData
         * @return
         * @throws JSONException
         */

        private Flood setFloodDataValues(String floodLineData)
                throws JSONException {
            //Log.v("tag", "returning complete flood  floodLineData: " + floodLineData);
            StringTokenizer floodLine = new StringTokenizer(floodLineData, ";");
            int i = 1;

            HashMap<String, String> floodMap = new HashMap<String, String>();
            while (floodLine.hasMoreElements() && i < 13) {
                String floodAttributes = floodLine.nextElement().toString();

                if (floodAttributes.contains("\\")) {
                    floodAttributes = stripCharacter(floodAttributes);
                }
                if (i == 1)
                    floodMap.put("AreasDataId", floodAttributes);
                if (i == 2)
                    floodMap.put("AreaId", floodAttributes);
                if (i == 3)
                    floodMap.put("Country", floodAttributes);
                if (i == 4)
                    floodMap.put("AlertLevel", floodAttributes);
                if (i == 5)
                    floodMap.put("Description", floodAttributes);
                if (i == 6)
                    floodMap.put("TypePointArea", floodAttributes);
                if (i == 7)
                    floodMap.put("PointsInJsonFormat", floodAttributes);
                if (i == 8)
                    floodMap.put("PointsNumber", floodAttributes);
                if (i == 9)
                    floodMap.put("BoundingBoxLonMin", floodAttributes);
                if (i == 10)
                    floodMap.put("BoundingBoxLonMax", floodAttributes);
                if (i == 11)
                    floodMap.put("BoundingBoxLatMin", floodAttributes);
                if (i == 12)
                    floodMap.put("BoundingBoxLatMax", floodAttributes);
                i++;
            }

            ArrayList<FloodLocation> locationArray = new ArrayList();
            locationArray = getListOfPointers(floodMap);
            Flood flood = new Flood(locationArray, floodMap.get("Country")
                    .toString(), floodMap.get("BoundingBoxLatMin").toString(),
                    floodMap.get("BoundingBoxLatMax").toString(), floodMap.get(
                    "BoundingBoxLonMin").toString(), floodMap.get(
                    "BoundingBoxLonMax").toString(), floodMap.get(
                    "AlertLevel"));
            return flood;
        }

        /**
         * Parse JSON Points Data as set in ArrayList for Location
         *
         * @throws JSONException
         */

        private ArrayList<FloodLocation> getListOfPointers(
                HashMap<String, String> floodMap) throws JSONException {
            ArrayList<FloodLocation> locationArray = new ArrayList();
            String pointsInJson = floodMap.get("PointsInJsonFormat");
            JSONObject jsonResponse;
            jsonResponse = new JSONObject(pointsInJson);
            JSONArray pointsArray = jsonResponse.getJSONArray("Points");

            for (int i = 0; i < pointsArray.length(); i++) {
                JSONObject jsonObj = pointsArray.getJSONObject(i);
                String valid = "";
                String xVal = "";
                String yVal = "";
                String coordinatesType = "";
                /**
                 * This is needed as not always all values are present
                 */

                if (jsonObj.has("Valid"))
                    valid = jsonObj.get("Valid").toString();
                if (jsonObj.has("X"))
                    xVal = jsonObj.get("X").toString();
                if (jsonObj.has("Y"))
                    yVal = jsonObj.get("Y").toString();
                if (jsonObj.has("CoordinatesType"))
                    coordinatesType = jsonObj.get("CoordinatesType").toString();


                FloodLocation loc = new FloodLocation(xVal, yVal, coordinatesType, valid);
                locationArray.add(loc);

            }

            return locationArray;
        }


        /**
         * Stripping / from Points JSON
         *
         * @param s
         * @return
         */

        private String stripCharacter(String s) {

            return s.replace("\\", "");
        }


    }
}