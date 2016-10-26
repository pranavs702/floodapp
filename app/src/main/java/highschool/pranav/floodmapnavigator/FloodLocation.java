package highschool.pranav.floodmapnavigator;

/**
 * Created by pranavandprathik on 10/19/16.
 */

/**
 * Points Data that is in JSON array is stored in this object
 * This will be used to plot the path in the form of tiles
 */

public class FloodLocation {

    private int x;
    private int y;
    private String coordinatesType;
    private String valid;

    public FloodLocation(int x, int y, String coordinatesType, String valid) {
        this.x = x;
        this.y = y;
        this.coordinatesType = coordinatesType;
        this.valid = valid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getCoordinatesType() {
        return coordinatesType;
    }

    public String isValid() {
        return valid;
    }

}
