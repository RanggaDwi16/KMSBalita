package com.example.kmsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kmsapp.helper.ActionCallback;
import com.example.kmsapp.helper.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PosyanduFormActivity extends AppCompatActivity {
    String TAG = Config.DEBUG_TAG;
    boolean debugMode = Config.DEBUG_MODE;
    SharedPreferences mSharedPreferences;
    PosyanduFormActivity mContext;
    String mUserToken = "";

    TextView tvTitle, tvSubtitle;
    EditText etNama, etKeterangan;
    Button btnSubmit, btnDelete;
    boolean edit = false;
    String id = "0";
    String nama = "";
    String keterangan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posyandu_form);
        mSharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(Config.SP_USER_TOKEN, "");
        mContext = PosyanduFormActivity.this;
        id = getIntent().getExtras().getString("POSY_ID", "0");
        if(!id.equals("0")){
            edit = true;
            nama = getIntent().getExtras().getString("POSY_NAMA", "");
            keterangan = getIntent().getExtras().getString("POSY_KETERANGAN", "");
        }
        if(mUserToken.equals("")){
            Config.forceLogout(mContext);
        }
        bind(new ActionCallback() {
            @Override
            public void onActionDone() {

            }
        });
    }


    private void bind(ActionCallback callback){
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        etKeterangan = findViewById(R.id.etKeterangan);
        etNama = findViewById(R.id.etNama);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnDelete = findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapus();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSubmit();
            }
        });
        callback.onActionDone();
        mode();
    }


    private void mode(){
        Log.d(TAG, "mode: " + edit);
        if(edit){
            tvTitle.setText("Halaman Edit Posyandu");
            tvSubtitle.setText("Edit Posyandu");
            btnSubmit.setText("Update");

            etNama.setText(nama);
            etKeterangan.setText(keterangan);
        }else{
            tvTitle.setText("Halaman Tambah Posyandu");
            tvSubtitle.setText("Tambah Posyandu");
            btnSubmit.setText("Tambah");
            btnDelete.setVisibility(View.INVISIBLE);
        }
        //
    }


    private void hapus(){
        String url = Config.BASE_URL + "/posyandu_delete";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseString) {
                Log.d(TAG, "hapus.onResponse : " + responseString);
                JSONObject response = null;
                try {
                    response = new JSONObject(responseString);
                    final String status = response.optString(Config.RESPONSE_STATUS_FIELD);
                    final String message = response.optString(Config.RESPONSE_MESSAGE_FIELD);
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    if(status.equalsIgnoreCase(Config.RESPONSE_STATUS_VALUE_SUCCESS)) {
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


    private void doSubmit(){
        String nama = etNama.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();

        if (TextUtils.isEmpty(nama)){
            etNama.setError("Mohon isikan nama posyandu");
            return;
        }
        if(TextUtils.isEmpty(keterangan)){
            etKeterangan.setError("Mohon isikan keterangan");
            return;
        }

        String url = Config.BASE_URL + "/posyandu_update";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("keterangan", keterangan);
        params.put("nama", nama);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseString) {
                Log.d(TAG, "doSubmit.onResponse : " + responseString);
                JSONObject response = null;
                try {
                    response = new JSONObject(responseString);
                    final String status = response.optString(Config.RESPONSE_STATUS_FIELD);
                    final String message = response.optString(Config.RESPONSE_MESSAGE_FIELD);
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    if(status.equalsIgnoreCase(Config.RESPONSE_STATUS_VALUE_SUCCESS)) {
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