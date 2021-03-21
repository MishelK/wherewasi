package actions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import models.User;

public class actions {

//    static final String BE_URL = "https://wherewasi-be.herokuapp.com/";
    static final String BE_URL = "http://192.168.1.178:3030/";
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


}
