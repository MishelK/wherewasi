package com.kdkvit.wherewasi.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.LocationsGroup;
import models.MyLocation;

public class General {

    private static final int TIME_BETWEEN_NEW_LOCATIONS = 1000 * 60 * 15;

    public static List<LocationsGroup> getLocationsGroup(List<MyLocation> locations) {
        List<LocationsGroup> groups = new ArrayList<>();
        MyLocation lastLocation = null;
        for (MyLocation location : locations) {
            if (lastLocation == null || (location.getStartTime().getTime() - lastLocation.getStartTime().getTime()) > TIME_BETWEEN_NEW_LOCATIONS) {
                groups.add(0,new LocationsGroup()); //Insert on top of the list
            }
            groups.get(0).add(location); //Add location to the top of the group
            lastLocation = location;
        }
        return groups;
    }

    public static boolean checkIfLocationInGroup(LocationsGroup group,MyLocation location){
        if(group.size()==0) return true;
        return (location.getStartTime().getTime() - group.getLastLocation().getStartTime().getTime() <= TIME_BETWEEN_NEW_LOCATIONS);
    }
}

