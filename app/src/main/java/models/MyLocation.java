package models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class MyLocation implements Serializable, ClusterItem {

    private long id;
    private double latitude;
    private double longitude;
    private String provider;
    private long startTime;
    private long endTime;
    private long updateTime;
    private float accuracy;

    private LatLng latLng;

    //Address
    private String adminArea;
    private String countryCode;
    private String featureName;
    private String locality;
    private String subAdminArea;
    private String addressLine;

    public MyLocation(long id,double latitude, double longitude, String provider,float accuracy){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.updateTime = System.currentTimeMillis();
        this.accuracy = accuracy;
        initLatLang();
    }

    public MyLocation(double latitude, double longitude, String provider,long startTime,long endTime,float accuracy){
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.updateTime = System.currentTimeMillis();
        this.startTime = startTime;
        this.endTime = endTime;
        this.accuracy = accuracy;
        initLatLang();
    }

    public MyLocation(double latitude, double longitude, String provider,long updateTime){
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.updateTime = updateTime;
        initLatLang();
    }

    private void initLatLang() {
        this.latLng = new LatLng(this.latitude,this.longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        initLatLang();
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        initLatLang();
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public void setSubAdminArea(String subAdminArea) {
        this.subAdminArea = subAdminArea;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyLocation that = (MyLocation) o;
        return id == that.id &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Float.compare(that.accuracy, accuracy) == 0 &&
                Objects.equals(provider, that.provider) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(adminArea, that.adminArea) &&
                Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(featureName, that.featureName) &&
                Objects.equals(locality, that.locality) &&
                Objects.equals(subAdminArea, that.subAdminArea) &&
                Objects.equals(addressLine, that.addressLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, latitude, longitude, provider, startTime, endTime, updateTime, accuracy, adminArea, countryCode, featureName, locality, subAdminArea, addressLine);
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return this.latLng;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
