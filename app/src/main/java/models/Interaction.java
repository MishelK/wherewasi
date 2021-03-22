package models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Interaction implements Parcelable {

    private String uuid;
    private Long firstSeen;
    private Long lastSeen;
    private long interactionID;
    private boolean positive = false;

    public Interaction(){}

    protected Interaction(Parcel in) {
        this.uuid = in.readString();
        this.firstSeen = in.readLong();
        this.lastSeen = in.readLong();
        this.interactionID = in.readInt();
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

    @NonNull
    @Override
    public String toString() {
        String string = "UUID : " + this.uuid + " FirstSeen : " + new Date(this.getFirstSeen()).toString() + "IsPositive : " + this.positive;
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
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }
}
