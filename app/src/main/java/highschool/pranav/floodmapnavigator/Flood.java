package highschool.pranav.floodmapnavigator;

/**
 * Created by pranavandprathik on 10/19/16.
 */

import java.util.ArrayList;
import highschool.pranav.floodmapnavigator.FloodLocation;

public class Flood {
    private ArrayList<FloodLocation> points;
    private String country;
    private String boundBoxMinLat;
    private String boundBoxMaxLat;

    private String boundBoxMinLon;
    private String boundBoxMaxLon;
    private String alertLevel;
    private int pointsNumber;

    Flood(ArrayList<FloodLocation> pointsIn, String countryIn,
                 String bBMInLat, String bBMaxLat, String bMinLon, String bMaxLon,
                 String alertLevelIn) {
        points = pointsIn;
        country = countryIn;
        boundBoxMinLat = bBMInLat;
        boundBoxMaxLat = bBMaxLat;
        boundBoxMinLon = bMinLon;
        boundBoxMaxLon = bMaxLon;
        alertLevel = alertLevelIn;

    }



    public ArrayList<FloodLocation> getPoints() {
        return points;
    }

    public String getCountry() {
        return country;
    }

    public String getBoundBoxMinLat() {
        return boundBoxMinLat;
    }

    public String getBoundBoxMaxLat() {
        return boundBoxMaxLat;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public String getBoundBoxMinLon() {
        return boundBoxMinLon;
    }

    public String getBoundBoxMaxLon() {
        return boundBoxMaxLon;
    }
    public int getPointsNumber() {
        return pointsNumber;
    }

}