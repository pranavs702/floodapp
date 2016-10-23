package highschool.pranav.floodmapnavigator;

/**
 * Created by pranavandprathik on 10/19/16.
 */

import android.location.Location;

import java.util.ArrayList;
import highschool.pranav.floodmapnavigator.FloodLocation;

public class Flood {
    private ArrayList<FloodLocation> points;
    private String country;
    private Location boundBoxMin;
    private Location boundBoxMax;

    private int alertLevel;
    private int pointsNumber;

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

}