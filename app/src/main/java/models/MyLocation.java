package models;

import java.io.Serializable;
import java.util.Date;

public class MyLocation implements Serializable {

    private long id;
    private double latitude;
    private double longitude;
    private String provider;
    private Date startTime;
    private Date endTime;
    private Date updateTime;

    //Address
    private String adminArea;
    private String countryCode;
    private String featureName;
    private String locality;
    private String subAdminArea;
    private String addressLine;

    public MyLocation(long id,double latitude, double longitude, String provider){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.updateTime = new Date();
    }

    public MyLocation(double latitude, double longitude, String provider,Date startTime,Date endTime){
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.updateTime = new Date();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public MyLocation(double latitude, double longitude, String provider,Date updateTime){
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.updateTime = updateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
