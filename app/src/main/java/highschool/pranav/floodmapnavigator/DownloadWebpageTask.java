package highschool.pranav.floodmapnavigator;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.google.android.gms.maps.model.LatLng;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

    public interface FloodAssyncResponse {
        //define any methods
        public void processFloodData(ArrayList<Flood> floods);

    }

    private final String FLOOD_MERGE_URL = "http://www.gdacs.org/floodmerge/data.aspx";
    public FloodAssyncResponse responseDelegate;
    private ArrayList<Flood> floodArray;
    private ArrayList<LatLng> latLngArrayList;

    @Override
    protected void onPreExecute() {
        Log.v("tag", "On pre execute");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        responseDelegate.processFloodData(floodArray);
    }

    @Override
    protected String doInBackground(String... params) {
        Log.v("tag", "do in background");
        return queryFloodData();

    }


    /**
     * Querying entire Flood Data
     * This will access live data from FLOOD_MERGE_URL
     * After checking the internet connection
     *
     * @return
     */
    private String queryFloodData() {
        floodArray = new ArrayList<Flood>();
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

        /**
         * http connection is opened to the flood website url
         */
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
        /**
         * Entier data is downloaded and header is stripped
         * This also stores all flood data that is parsed in flood array list
         */
        if (floodStream != null) {
            floodReader = new BufferedReader(new InputStreamReader(floodStream));
            try {
                floodFileData = floodReader.readLine();
                String[] floodData = floodFileData.split("<br>");
                for (String s : floodData) {
                    if (!s.contains("AreasDataId")) {

                        Flood flood = setFloodDataValues(s);
                        floodArray.add(flood);
                        latLngArrayList = flood.getMaxMinValues(flood.getPoints(),flood.getBoundBoxMin().getLatitude(),flood.getBoundBoxMax().getLatitude(),flood.getBoundBoxMin().getLongitude(),flood.getBoundBoxMax().getLongitude());
                        //Log.v("tag", "reading flood data " + flood.getCountry());//This is for sample to see if parsing works
                        flood.setLatLngArrayList(latLngArrayList);//Set the LatLng Bondaries for tiles in Flood Object, This will be used for setting markers later
                    }
                }


            } catch (IOException e) {
                Log.v("tag", "io exception in reading flood data " + e.getLocalizedMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return floodFileData;
        //Log.v("tag", "returning complete flood  file data: " + floodFileData);Not needed as parsing is successful
    }

    /**
     * Each of the line data is parsed here
     * and stored in Flood object
     * For Points data it is converted to JSON and stored in FLoodLocation obejct as array list
     * @param floodLineData
     * @return
     * @throws JSONException
     */

    private Flood setFloodDataValues(String floodLineData)
            throws JSONException {
        //Log.v("tag", "returning complete flood  floodLineData: " + floodLineData);
        //Parse data by ;
        StringTokenizer floodLine = new StringTokenizer(floodLineData, ";");
        int i = 1;

        HashMap<String, String> floodMap = new HashMap<String, String>();
        while (floodLine.hasMoreElements() && i < 13) {
            String floodAttributes = floodLine.nextElement().toString();
            //THis is used for stripping \ before parsing JSON points object

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

        double boundingBoxLatMin = Double.parseDouble(floodMap.get("BoundingBoxLatMin"));
        double boundingBoxLonMin = Double.parseDouble(floodMap.get("BoundingBoxLonMin"));

        Location boundBoxMin = new Location("");
        boundBoxMin.setLatitude(boundingBoxLatMin);
        boundBoxMin.setLongitude(boundingBoxLonMin);

        double boundingBoxLatMax = Double.parseDouble(floodMap.get("BoundingBoxLatMax"));
        double boundingBoxLonMax = Double.parseDouble(floodMap.get("BoundingBoxLonMax"));

        Location boundBoxMax = new Location("");
        boundBoxMax.setLatitude(boundingBoxLatMax);
        boundBoxMax.setLongitude(boundingBoxLonMax);

        int alertLevel = Integer.parseInt(floodMap.get("AlertLevel"));

        ArrayList<FloodLocation> locationArray = new ArrayList();
        locationArray = getListOfPointers(floodMap);
        Flood flood = new Flood(locationArray, floodMap.get("Country")
                .toString(), boundBoxMin, boundBoxMax, alertLevel);
        return flood;
    }

    /**
     * Parse JSON Points Data as set in ArrayList for Location
     * Continuation of JSON parsing and accessing Pointers data
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


            FloodLocation loc = new FloodLocation(Integer.parseInt(xVal), Integer.parseInt(yVal), coordinatesType, valid);
            locationArray.add(loc);

        }

        return locationArray;
    }


    /**
     * Stripping / from Points JSON
     * This is needed to Parse the JSON as there is extra /
     * @param s
     * @return
     */

    private String stripCharacter(String s) {

        return s.replace("\\", "");
    }


}
