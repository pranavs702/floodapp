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
        double xScale = (maxLat - minLat) / xDiff;
        double yScale = (maxLong - minLong) / yDiff;
        //Log.v("tag", "reading xScale " + xScale);
        //Log.v("tag", "reading yScale " + yScale);

        double newLat =0;
        double newLong = 0;
        while (xSize > 0 || ySize > 0) {

            if (xSize!=0)
                newLat = minLat + xScale;
            if (ySize!=0)
                newLong = minLong + yScale;
            LatLng latLng = new LatLng(newLat,newLong);
            Log.v("tag", "reading lattitude " + newLat);
            Log.v("tag", "reading longitude " + newLong);

            xSize--;
            ySize--;
            latLngArrayList.add(latLng);
        }
        return latLngArrayList;
    }

}

