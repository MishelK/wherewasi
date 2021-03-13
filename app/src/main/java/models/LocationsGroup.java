package models;

import java.util.ArrayList;
import java.util.Date;
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

    public Date getStartTime(){
        int size;
        if((size = this.size())>0) {
            return locations.get(size - 1).getStartTime();
        }
        return null;
    }

    public Date getEndTime(){
        if(this.size()>0) {
            return locations.get(0).getEndTime();
        }
        return null;
    }

    public void add(MyLocation location) {
        locations.add(0,location);
    }

    public MyLocation getLastLocation(){
        return locations.get(0);
    }
}
