package models;

import androidx.annotation.NonNull;

public class BtDevice {

    private Long firstSeen;
    private Long lastSeen;
    private String uuid;


    public BtDevice() {
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

    @NonNull
    @Override
    public String toString() {
        String string = " UUID : " + this.uuid;
        return string;
    }
}
