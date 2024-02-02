package com.example.kmsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kmsapp.helper.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PosyanduListActivity extends AppCompatActivity {

    String TAG = Config.DEBUG_TAG;
    SharedPreferences mSharedPreferences;
    PosyanduListActivity mContext;
    String mUserToken = "";
    String mUserJenis = "";

    LinearLayout divContainer, divBtnTambah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posyandu_list);

        mSharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(Config.SP_USER_TOKEN, "");
        mUserJenis = mSharedPreferences.getString(Config.SP_USER_TYPE, "");
        if(mUserToken.equals("")){
            Config.forceLogout(mContext);
        }
        mContext = PosyanduListActivity.this;

        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderList("");
    }

    private void bindViews(){
        divContainer = findViewById(R.id.divContainer);
        divBtnTambah = findViewById(R.id.divBtnTambah);
        divBtnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, PosyanduFormActivity.class);
                i.putExtra("POSY_ID", "0");
                startActivity(i);
            }
        });
    }


    private void renderList(String keyword){
        divContainer.removeAllViews();
        Map<String, String> params = new HashMap<>();
        //params.put("by", qry);
        String url = Config.BASE_URL + "/posyandu_get";
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseString) {
                Log.d(TAG, "onResponse : " + responseString);
                JSONObject response = null;
                try {
                    response = new JSONObject(responseString);
                    final String status = response.optString(Config.RESPONSE_STATUS_FIELD);
                    final String message = response.optString(Config.RESPONSE_MESSAGE_FIELD);
                    if(status.equalsIgnoreCase(Config.RESPONSE_STATUS_VALUE_SUCCESS)) {
                        JSONArray payload = response.optJSONArray(Config.RESPONSE_PAYLOAD_FIELD);
                        for(int i = 0; i < payload.length(); i++){
                            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = layoutInflater.inflate(R.layout.itemrow_posyandu, null);

                            JSONObject _posyandu = payload.optJSONObject(i);
                            CardView cvItem = view.findViewById(R.id.cvItem);
                            TextView tvNama = view.findViewById(R.id.tvNama);
                            TextView tvKeterangan = view.findViewById(R.id.tvKeterangan);
                            tvNama.setText("" + _posyandu.optString("POSY_NAMA"));
                            tvKeterangan.setText("" + _posyandu.optString("POSY_KETERANGAN"));
                            cvItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(mContext, PosyanduFormActivity.class);
                                    i.putExtra("POSY_ID", _posyandu.optString("POSY_ID"));
                                    i.putExtra("POSY_NAMA", _posyandu.optString("POSY_NAMA"));
                                    i.putExtra("POSY_KETERANGAN", _posyandu.optString("POSY_KETERANGAN"));
                                    startActivity(i);
                                }
                            });
                            divContainer.addView(view);
                        }
                    }else{
                        finish();
                    }
                }
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    String errorInfo1 = error.getMessage();
                    String errorInfo2 = error.getLocalizedMessage();
                    Log.d(TAG, "onError: " + error);
                    Log.d(TAG, "onError code: " + statusCode);
                    Log.d(TAG, "onError code: " + errorInfo1);
                    Log.d(TAG, "onError code: " + errorInfo2);
                }
                Toast.makeText(mContext, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("auth-token", mUserToken);
                return headers;
            }
            @Override
            public RetryPolicy getRetryPolicy() {
                return Config.getRetryPolicy();
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }


}