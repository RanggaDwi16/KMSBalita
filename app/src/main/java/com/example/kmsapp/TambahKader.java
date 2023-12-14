package com.example.kmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class TambahKader extends AppCompatActivity {
    String TAG = Config.DEBUG_TAG;
    boolean debugMode = Config.DEBUG_MODE;
    SharedPreferences mSharedPreferences;
    TambahKader mContext;
    String mUserToken = "";

    EditText etEmail, etPassword, etNama, etHP;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kader);
        mSharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(Config.SP_USER_TOKEN, "");
        mContext = TambahKader.this;
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
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etPassword = findViewById(R.id.editTextTextPassword);
        etNama = findViewById(R.id.editTextTextPersonName2);
        etHP = findViewById(R.id.editTextPhone);
        btnSubmit = findViewById(R.id.button4);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegister();
            }
        });
        callback.onActionDone();
    }


    private void doRegister(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String hp = etHP.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            etEmail.setError("Email is required.");
            return;
        }
        if(TextUtils.isEmpty(password)){
            etPassword.setError("Password is required.");
            return;
        }
        if (TextUtils.isEmpty(email)){
            etNama.setError("Nama is required.");
            return;
        }
        if(TextUtils.isEmpty(password)){
            etHP.setError("No. HP is required.");
            return;
        }

        String url = Config.BASE_URL + "/auth_register";
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("nama", nama);
        params.put("hp", hp);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseString) {
                Log.d(TAG, "doLogin.onResponse : " + responseString);
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