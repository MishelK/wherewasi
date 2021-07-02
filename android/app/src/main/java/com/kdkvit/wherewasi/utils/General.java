package com.kdkvit.wherewasi.utils;

import android.content.Context;
import android.content.res.Resources;

import com.kdkvit.wherewasi.BuildConfig;
import com.kdkvit.wherewasi.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import models.Interaction;
import models.LocationsGroup;
import models.MyLocation;

public class General {

    private static final int TIME_BETWEEN_NEW_LOCATIONS = Configs.TIME_BETWEEN_NEW_LOCATIONS;
    private static final int TIME_BETWEEN_LOCATION_AND_INTERACTION = Configs.TIME_BETWEEN_LOCATION_AND_INTERACTION;

    /**
     * @param locations List<MyLocation> - locations to aggregate
     * @param interactions List<Interaction> - Interactions to aggregate
     * @param minTime min time to of group to filter
     * @param onlyInteractions if to return only groups with interactions
     * @return List of groups of locations with interactions
     */
    public static List<LocationsGroup> getLocationsGroup(List<MyLocation> locations, List<Interaction> interactions, int minTime, boolean onlyInteractions) {
        List<LocationsGroup> groups = new ArrayList<>();
        if (locations.size() == 0) return groups;
        MyLocation lastLocation = null;
        //run over all the locations aggregate close locations
        for (MyLocation location : locations) {
            if (lastLocation == null || (location.getStartTime() - lastLocation.getStartTime()) > TIME_BETWEEN_NEW_LOCATIONS) {
                groups.add(0, new LocationsGroup()); //Insert on top of the list
            }
            groups.get(0).addLocation(location); //Add location to the top of the group
            lastLocation = location;
        }

        //run over all the interactions and add them to the relevant location groups
        for (Interaction interaction : interactions) {
            for (int j = 0; j < groups.size(); j++) {
                if (((interaction.getLastSeen() - groups.get(j).getStartTime().getTime()) >= TIME_BETWEEN_LOCATION_AND_INTERACTION)
                        && (interaction.getLastSeen() - groups.get(j).getEndTime().getTime() < 0)) {
                    groups.get(j).addInteraction(interaction);
                }else if(interaction.getFirstSeen() - groups.get(j).getEndTime().getTime() > 0) {
                    break;
                }
            }
        }

        //Filter groups if needed
        if(minTime>0 || onlyInteractions){
            List<LocationsGroup> temp = new ArrayList<>();
            for(LocationsGroup locationsGroup: groups) {
                boolean enoughTime = (locationsGroup.getEndTime().getTime() - locationsGroup.getStartTime().getTime()) >= minTime * 1000 * 60;
                if (enoughTime && (!onlyInteractions || locationsGroup.interactionsSize() > 0)) {
                    temp.add(locationsGroup);
                }
            }
            groups = temp;
        }

        return groups;
    }

    /**
     * @param group LocationsGroup to check
     * @param location MyLocation - require locations to search
     * @return if the location is in the group
     */
    public static boolean checkIfLocationInGroup(LocationsGroup group,MyLocation location){
        if(group.locationsSize()==0) return true;
        return (location.getStartTime() - group.getLastLocation().getStartTime() <= TIME_BETWEEN_NEW_LOCATIONS);
    }

}

