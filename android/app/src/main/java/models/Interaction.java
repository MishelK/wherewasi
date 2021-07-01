package models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Interaction implements Parcelable {

    private String uuid;
    private Long firstSeen;
    private Long lastSeen;
    private int rssi = 0;
    private long interactionID;
    private boolean isDangerous; // Confirmed by rssi strength
    private boolean isPositive = false; // Confirmed by user who declares themselves positive
    private boolean isConfirmedSameSpace = false; // Confirmed by ultra sound transmission
    private long lastConfirmationRequest;

    public Interaction(){}

    public Interaction(String uuid, Long firstSeen, Long lastSeen, int rssi, long interactionID, boolean isDangerous, boolean isPositive, boolean isConfirmedSameSpace) {
        this.uuid = uuid;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.rssi = rssi;
        this.interactionID = interactionID;
        this.isDangerous = isDangerous;
        this.isPositive = isPositive;
        this.isConfirmedSameSpace = isConfirmedSameSpace;
    }

    protected Interaction(Parcel in) {
        this.uuid = in.readString();
        this.firstSeen = in.readLong();
        this.lastSeen = in.readLong();
        this.interactionID = in.readInt();
        this.rssi = in.readInt();
        this.isDangerous = in.readInt() == 1;
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

    public long getInteractionID() {
        return interactionID;
    }

    public void setInteractionID(long interactionID) {
        this.interactionID = interactionID;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public boolean isDangerous() {
        return isDangerous;
    }

    public void setIsDangerous(boolean isDangerous) {
        this.isDangerous = isDangerous;
    }

    public boolean isConfirmedSameSpace() {return isConfirmedSameSpace; }

    public void setConfirmedSameSpace(boolean confirmedSameSpace) { this.isConfirmedSameSpace = confirmedSameSpace; }

    public long getLastConfirmationRequest() { return lastConfirmationRequest; }

    public void setLastConfirmationRequest(long lastConfirmationRequest) { this.lastConfirmationRequest = lastConfirmationRequest; }

    @NonNull
    @Override
    public String toString() {
        String string = "UUID : " + this.uuid + " FirstSeen : " + new Date(this.getFirstSeen()).toString() + "IsPositive : " + this.isPositive;
        return string;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeLong(lastSeen);
        parcel.writeLong(lastSeen);
        parcel.writeLong(interactionID);
        parcel.writeInt(rssi);
        parcel.writeInt(isDangerous ? 1 : 0);
    }

    public static final Creator<Interaction> CREATOR = new Creator<Interaction>() {
        @Override
        public Interaction createFromParcel(Parcel in) {
            return new Interaction(in);
        }

        @Override
        public Interaction[] newArray(int size) {
            return new Interaction[size];
        }
    };

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        this.isPositive = positive;
    }
}
