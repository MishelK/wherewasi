package models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private String deviceId;
    private String fcmId;
    private String bleId;

    public User(){

    }

    public User(int id, String name, String deviceId, String fcmId, String bleId) {
        this.id = id;
        this.name = name;
        this.deviceId = deviceId;
        this.fcmId = fcmId;
        this.bleId = bleId;
    }

    public User(JSONObject obj) {
        try {
            this.id = Integer.parseInt(obj.getString("id"));
            if(obj.has("name")) {
                this.name = obj.getString("name");
            }
            if(obj.has("ble_id")) {
                this.bleId = obj.getString("ble_id");
            }
            if(obj.has("device_id")) {
                this.deviceId = obj.getString("device_id");
            }
            if(obj.has("fcm_id")) {
                this.fcmId = obj.getString("fcm_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public String getBleId() {
        return bleId;
    }

    public void setBleId(String bleId) {
        this.bleId = bleId;
    }
}
