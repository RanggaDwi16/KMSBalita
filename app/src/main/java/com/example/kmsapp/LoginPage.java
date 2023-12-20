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
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kmsapp.helper.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {
    String TAG = Config.DEBUG_TAG;
    boolean debugMode = Config.DEBUG_MODE;
    SharedPreferences mSharedPreferences;
    LoginPage mContext;
    String mUserToken = "";

    EditText _email, _password;
    Button _btnLogin;
    Spinner _spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mSharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(Config.SP_USER_TOKEN, "");
        mContext = LoginPage.this;
        if(!mUserToken.equals("")){
            Log.d(TAG, "onCreate: mUserToken: " + mUserToken);
            startActivity(new Intent(mContext, HomePage.class));
            finish();
        }

        _email = (EditText) findViewById(R.id.txtEmail);
        _password = (EditText) findViewById(R.id.txtPassword);
        _btnLogin = (Button) findViewById(R.id.btnLogin);
        _spinner = (Spinner) findViewById(R.id.spinner);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.role, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        _spinner.setAdapter(adapter);
//
//        _spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // Code to handle item selection
//                String selectedRole = (String) parentView.getItemAtPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Code to handle no item selected
//            }
//        });

        // Login Button redirect to home page
        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

//        if(debugMode){
//            _email.setText("admin1@gmail.com");
//            _password.setText("12345678");
//            //doLogin();
//        }

    }


    private void doLogin(){
        String email = _email.getText().toString().trim();
        String password = _password.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            _email.setError("Email is required.");
            return;
        }
        if(TextUtils.isEmpty(password)){
            _password.setError("Password is required.");
            return;
        }

        String url = Config.BASE_URL + "/auth_login";
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
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
                        JSONObject payload = response.optJSONObject(Config.RESPONSE_PAYLOAD_FIELD);
                        mSharedPreferences.edit().putString(Config.SP_USER_ID, payload.optString("USER_ID")).commit();
                        mSharedPreferences.edit().putString(Config.SP_USER_TOKEN, payload.optString("USER_TOKEN")).commit();
                        mSharedPreferences.edit().putString(Config.SP_USER_TYPE, payload.optString("USER_TYPE")).commit();
                        mSharedPreferences.edit().putString(Config.SP_USER_NAMA, payload.optString("USER_NAMA")).commit();
                        mSharedPreferences.edit().putString(Config.SP_USER_HP, payload.optString("USER_HP")).commit();
                        mSharedPreferences.edit().putString(Config.SP_USER_EMAIL, payload.optString("USER_EMAIL")).commit();
                        mSharedPreferences.edit().putString(Config.SP_USER_AVATAR_PATH, payload.optString("USER_AVATAR_PATH")).commit();
                        startActivity(new Intent(mContext, HomePage.class));
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