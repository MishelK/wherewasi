package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocationsGroup {

    private List<MyLocation> locations;

    private List<Interaction> interactions;

    public LocationsGroup(){
        this.locations = new ArrayList<>();
        this.interactions = new ArrayList<>();
    }

    public List<MyLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<MyLocation> locations) {
        this.locations = locations;
    }

    public int locationsSize(){
        return locations.size();
    }

    public Date getStartTime(){
        int size;
        if((size = this.locationsSize())>0) {
            Date date = new Date();
            date.setTime(locations.get(size - 1).getStartTime());
            return date;
        }
        return null;
    }

    public Date getEndTime(){
        if(this.locationsSize()>0) {
            Date date = new Date();
            date.setTime(locations.get(0).getEndTime());
            return date;
        }
        return null;
    }

    public void addLocation(MyLocation location) {
        locations.add(0,location);
    }

    public MyLocation getLastLocation(){
        return locations.get(0);
    }

    public List<Interaction> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<Interaction> interactions) {
        this.interactions = interactions;
    }

    public void addInteraction(Interaction interaction) {
        interactions.add(interaction);
    }

    public int interactionsSize(){
        return interactions.size();
    }

    public List<Interaction> getPositiveInteractions() {
        List<Interaction> positives = new ArrayList<>();
        for (int i=0;i<this.interactionsSize();i++){
            if(this.interactions.get(i).isPositive()){
                positives.add(interactions.get(i));
            }
        }
        return positives;
    }
}
