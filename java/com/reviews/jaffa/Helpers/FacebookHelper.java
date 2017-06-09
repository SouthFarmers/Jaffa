package com.reviews.jaffa.Helpers;

import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.reviews.jaffa.Adapters.CriticReviewsAdapter;
import com.reviews.jaffa.Adapters.FriendReviewsAdapter;
import com.reviews.jaffa.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gautham on 5/30/17.
 */

public class FacebookHelper {

    String access_token = "EAAFkKlScYZAcBAF1TLu9iurW5FO6vJMgKAPabNP603mSdsGPRnOixeKEZB9J4w10fpNCTY%20tOg6Xj4EhEF4X7f67Wpb8wkDKM4uUZCU2oDS6cOfsbXnzNb7lcSPJrjMyk5xzKhn9DjamXpEM%20W180Ha4ZCABCd3yYZD";
    Object resp;
    public Object getUserprofile(){

        String url = "https://graph.facebook.com/4?access_token="+access_token;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            resp = response;

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
        return resp;
    }


}
