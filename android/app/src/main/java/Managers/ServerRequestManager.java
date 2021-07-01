package Managers;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kdkvit.wherewasi.utils.Configs;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import models.User;

public class ServerRequestManager {

    static final String BE_URL = Configs.server_url;
//    static final String BE_URL = "http://192.168.1.178:3030/";
    static final String USERS_URL = BE_URL + "api/users/";


    public static void sendUserToBe(Context context, User user){
        final JSONObject rootObject = new JSONObject();
        try{
            RequestQueue queue = Volley.newRequestQueue(context);
            rootObject.put("device_id",user.getDeviceId());
            rootObject.put("ble_id",user.getBleId());
            rootObject.put("fcm_id",user.getFcmId());
            rootObject.put("name",user.getName());
            String url = USERS_URL+"register";
            Log.i("url",url);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);

                    int failure = obj.optInt("failure");
                    if (failure == 0){
                        User newUser = new User(obj);
                        SharedPreferencesUtils.setUser(context,newUser);
                        Log.i("new_user", String.valueOf(newUser.getId()));
                    }else{
                      //Something went wrong
                    }
                    } catch (JSONException e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("testrecording","error when sending recording "+error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return rootObject.toString().getBytes();
                }
            };
            queue.add(request);
        }catch (JSONException e){

        }
    }


    public interface ActionsCallback{
        void onSuccess() throws InterruptedException;
        void onFailure();
    }

    public static void sendConfirmationRequest(Context context, String uuid, ActionsCallback callback){
        User user = SharedPreferencesUtils.getUser(context);
        final JSONObject rootObject = new JSONObject();
        try{
            RequestQueue queue = Volley.newRequestQueue(context);
            rootObject.put("user_id", user.getDeviceId());
            rootObject.put("target_id", uuid);
            String url = USERS_URL+"sound_ping";
            Log.i("url",url);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject obj = null;
                        if (response.equals("success")){
                            try {
                                callback.onSuccess();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            callback.onFailure();
                        }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailure();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return rootObject.toString().getBytes();
                }
            };
            queue.add(request);
        }catch (JSONException e){
            callback.onFailure();
        }
    }

    public static void sendListeningSuccessful(Context context, String uuid, ActionsCallback callback){
        User user = SharedPreferencesUtils.getUser(context);
        final JSONObject rootObject = new JSONObject();
        try{
            RequestQueue queue = Volley.newRequestQueue(context);
            rootObject.put("user_id", user.getDeviceId());
            rootObject.put("target_id", uuid);
            String url = USERS_URL+"sound_received";
            Log.i("url",url);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (callback != null) {
                        try {
                            callback.onSuccess();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (callback != null)
                        callback.onFailure();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return rootObject.toString().getBytes();
                }
            };
            queue.add(request);
        }catch (JSONException e){
            callback.onFailure();
        }
    }

    public static void sendPositive(Context context, Long date, ActionsCallback callback){
        User user = SharedPreferencesUtils.getUser(context);
        final JSONObject rootObject = new JSONObject();
        try{
            RequestQueue queue = Volley.newRequestQueue(context);
            rootObject.put("device_id", user.getDeviceId());
            rootObject.put("mark_time", date);
            String url = USERS_URL+"mark_positive";
            Log.i("url",url);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);

                        int failure = obj.optInt("failure");
                        if (failure == 0){
                            callback.onSuccess();
                        }else{
                            callback.onFailure();
                        }
                    } catch (JSONException | InterruptedException e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailure();
                    Log.i("testrecording","error when sending recording "+error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return rootObject.toString().getBytes();
                }
            };
            queue.add(request);
        }catch (JSONException e){
            callback.onFailure();
        }
    }


}
