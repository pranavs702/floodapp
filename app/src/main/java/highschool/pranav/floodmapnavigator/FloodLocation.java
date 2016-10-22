package highschool.pranav.floodmapnavigator;

/**
 * Created by pranavandprathik on 10/19/16.
 */

public class FloodLocation {

    private String x;
    private String y;
    private String coordinatesType;
    private String valid;

    public FloodLocation(String x, String y, String coordinatesType, String valid) {
        this.x = x;
        this.y = y;
        this.coordinatesType = coordinatesType;
        this.valid = valid;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getCoordinatesType() {
        return coordinatesType;
    }

    public String isValid() {
        return valid;
    }

}
