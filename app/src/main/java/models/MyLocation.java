package models;

import java.io.Serializable;
import java.util.Date;

public class MyLocation implements Serializable {

    private double latitude;
    private double longitude;
    private String provider;
    private Date time;

    //Address
    private String adminArea;
    private String countryCode;
    private String featureName;
    private String locality;
    private String subAdminArea;
    private String addressLine;

    public MyLocation(double latitude, double longitude, String provider){
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.time = new Date();
    }

    public MyLocation(double latitude, double longitude, String provider,Date time){
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.time = time;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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
}
