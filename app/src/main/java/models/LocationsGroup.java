package models;

import java.util.ArrayList;
import java.util.List;

public class LocationsGroup {

    private List<MyLocation> locations;

    public LocationsGroup(){
        locations = new ArrayList<>();
    }

    public List<MyLocation> getLocations() {
        return locations;
    }


    public void setLocations(List<MyLocation> locations) {
        this.locations = locations;
    }

    public int size(){
        return locations.size();
    }
}
