package models;

import androidx.annotation.NonNull;

public class Interaction {

    private String uuid;
    private Long firstSeen;
    private Long lastSeen;
    private int interactionID;


    public Interaction() {
    }

    public Long getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Long firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getInteractionID() {
        return interactionID;
    }

    public void setInteractionID(int interactionID) {
        this.interactionID = interactionID;
    }

    @NonNull
    @Override
    public String toString() {
        String string = " UUID : " + this.uuid;
        return string;
    }
}
