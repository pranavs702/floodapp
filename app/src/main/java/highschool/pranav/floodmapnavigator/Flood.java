package highschool.pranav.floodmapnavigator;

/**
 * Created by pranavandprathik on 10/19/16.
 */

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import java.util.Collections;

import highschool.pranav.floodmapnavigator.FloodLocation;

import static java.util.Collection.*;

/**
 * Object to store Flood Data
 * This will be later used for plotting in the MAP
 */
public class Flood {
    private ArrayList<FloodLocation> points;
    private String country;
    private Location boundBoxMin;
    private Location boundBoxMax;

    private int alertLevel;



    private int pointsNumber;

    ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();

    Flood(ArrayList<FloodLocation> pointsIn, String countryIn,
          Location bBMIn, Location bBMax,
                 int alertLevelIn) {
        points = pointsIn;
        country = countryIn;
        boundBoxMax = bBMax;
        boundBoxMin = bBMIn;
        alertLevel = alertLevelIn;


    }


    public ArrayList<FloodLocation> getPoints() {
        return points;
    }

    public String getCountry() {
        return country;
    }

    public Location getBoundBoxMin() {
        return boundBoxMin;
    }

    public Location getBoundBoxMax() {
        return boundBoxMax;
    }

    public int getAlertLevel() {
        return alertLevel;
    }

    public int getPointsNumber() {
        return pointsNumber;
    }

    /**
     * Get Data from FloodLocation for X, Y Coordinates
     */

    public ArrayList<LatLng> getLatLngArrayList() {
        return latLngArrayList;
    }

    public void setLatLngArrayList(ArrayList<LatLng> latLngArrayList) {
        this.latLngArrayList = latLngArrayList;
    }

    /**
     * MaxLat - MinLat
     * -------------  = scale
     * (difference in value of max and min of x)
     * Scale + Min Lat
     * Same Applicable for Min Long
     * MaxLong - MinLong
     * -------------  = scale
     * (difference in value of max and min of y)
     * Scale + Min Long
     * Same Applicable for Min Long
     * Total
     * Total
     * This logic for tiles ca
     * @return
     */
    /* this.x = (lon + 180) * 360;
    this.y = (lat + 90) * 180; */
    // TO CONVERT X and Y into lat and lon - google GIS for more info
    // lon = x/360 - 180
    // lat = y/180 - 90
    public ArrayList<LatLng> getMaxMinValues(ArrayList<FloodLocation> poinstArray, Double minLat, Double maxLat, Double minLong, Double maxLong) {
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
        /**
         * Get the X and Y Values from ArrayList Separate and get Max and Min the values
         * Later + Size of X and Y ArrayList
         */
        ArrayList<Integer> xArrayList = new ArrayList<Integer>();
        ArrayList<Integer> yArrayList = new ArrayList<Integer>();
        for (FloodLocation point : poinstArray) {
            xArrayList.add(point.getX());
            yArrayList.add(point.getY());
        }
        int xMax = Collections.max(xArrayList);
        int xMin = Collections.min(xArrayList);
        int yMax = Collections.max(yArrayList);
        int yMin = Collections.min(yArrayList);
        int xSize = xArrayList.size();
        int ySize = yArrayList.size();
        int xDiff = xMax - xMin;
        int yDiff = yMax - yMin;
        double xScale = 360/4000.0;
        double yScale = 180/2000.0;
        //Log.v("tag", "reading xScale " + xScale);
        //Log.v("tag", "reading yScale " + yScale);

        double newLat =0;
        double newLong = 0;
//
        for(FloodLocation loc: poinstArray){
            int x = loc.getX();
            int y = loc.getY();
            double latitude = y*yScale - 90;
            double longitude = x*xScale - 180;
            LatLng llg = new LatLng(-1 *latitude, longitude);
            latLngArrayList.add(llg);
        }
        return latLngArrayList;
    }

}

