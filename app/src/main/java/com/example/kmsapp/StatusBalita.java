package com.example.kmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatusBalita extends AppCompatActivity {
    String TAG = Config.DEBUG_TAG;
    boolean debugMode = Config.DEBUG_MODE;
    SharedPreferences mSharedPreferences;
    StatusBalita mContext;
    String mUserToken = "";

    String posyandu = "";
    String tanggal = "";

    EditText etNama, etTinggi, etBerat, etKepala, etUmur;
    TextView tvHasilTinggi, tvHasilBerat, tvHasilKepala;
    Button btnCek, btnSimpan;
    Spinner spnJK;
    String selectedJK;
    Double tinggi;
    Double berat;
    Double kepala;
    Double umur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_balita);

        mSharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(Config.SP_USER_TOKEN, "");
        mContext = StatusBalita.this;
        if(mUserToken.equals("")){
            Config.forceLogout(mContext);
        }
        posyandu = getIntent().getExtras().getString("POSYANDU", "");
        tanggal = getIntent().getExtras().getString("TANGGAL", "");
        bind(new ActionCallback() {
            @Override
            public void onActionDone() {
                setupTrigger();
            }
        });
    }

    private void bind(ActionCallback callback){
        etNama = findViewById(R.id.etNama);
        etTinggi = findViewById(R.id.etTinggi);
        etBerat = findViewById(R.id.etBerat);
        etKepala = findViewById(R.id.etKepala);
        etUmur = findViewById(R.id.etUmur);
        tvHasilTinggi = findViewById(R.id.tvHasilTinggi);
        tvHasilBerat = findViewById(R.id.tvHasilBerat);
        tvHasilKepala = findViewById(R.id.tvHasilKepala);
        btnCek = findViewById(R.id.btnCek);
        btnSimpan = findViewById(R.id.btnSimpan);
        spnJK = findViewById(R.id.spnJK);

        btnCek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, BeratTinggiIdeal.class));
                finish();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpan();
            }
        });

        ArrayList<String> jkList = new ArrayList<>();
        jkList.add("Laki-laki");
        jkList.add("Perempuan");
        HashMap<String, String> jkObject = new HashMap<>();
        jkObject.put("Laki-laki", "GENDER_MALE");
        jkObject.put("Perempuan", "GENDER_FEMALE");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(StatusBalita.this, android.R.layout.simple_spinner_item, jkList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJK.setAdapter(adapter);
        spnJK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = spnJK.getSelectedItem().toString();
                selectedJK = jkObject.get(selected);
                cek();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spnJK.setSelection(0);

        callback.onActionDone();
    }


    private void setupTrigger(){
        tvHasilTinggi.setText("-");
        tvHasilBerat.setText("-");
        tvHasilKepala.setText("-");
        btnSimpan.setVisibility(View.INVISIBLE);

        etTinggi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String newText = charSequence.toString();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String v = editable.toString();
                cek();
            }
        });
        etBerat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                cek();
            }
        });
        etKepala.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                cek();
            }
        });
        etUmur.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                cek();
            }
        });
    }


    private void cek(){
        String v1 = etTinggi.getText().toString();
        String v2 = etBerat.getText().toString();
        String v3 = etKepala.getText().toString();
        String v4 = etUmur.getText().toString();
        if (v1.isEmpty() || v1.equals("0") || v1.startsWith(".")
                || v2.isEmpty() || v2.equals("0") || v2.startsWith(".")
                || v3.isEmpty() || v3.equals("0") || v3.startsWith(".")
                || v4.isEmpty() || v4.equals("0") || v4.startsWith(".")
        ){
            tvHasilTinggi.setText("-");
            tvHasilBerat.setText("-");
            tvHasilKepala.setText("-");
            btnSimpan.setVisibility(View.INVISIBLE);
            return;
        }
        btnSimpan.setVisibility(View.VISIBLE);

        tinggi = Double.valueOf(etTinggi.getText().toString());
        berat = Double.valueOf(etBerat.getText().toString());
        kepala = Double.valueOf(etKepala.getText().toString());
        umur = Double.valueOf(etUmur.getText().toString());
        String rTinggi = Config.getBalitaStatus(selectedJK + " TINGGI " + umur, tinggi).get(0);
        String rBerat = Config.getBalitaStatus(selectedJK + " BERAT " + umur, berat).get(0);
        String rKepala = Config.getBalitaStatus(selectedJK + " KEPALA " + umur, kepala).get(0);
        tvHasilTinggi.setText(rTinggi);
        tvHasilBerat.setText(rBerat);
        tvHasilKepala.setText(rKepala);
    }


    private void simpan(){
        String nama = etNama.getText().toString().trim();
        if(nama.equals("")){
            Toast.makeText(mContext, "Mohon isikan nama balita", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = Config.BASE_URL + "/tes_save";
        Map<String, String> params = new HashMap<>();
        params.put("posyandu", posyandu);
        params.put("tanggal", tanggal);
        params.put("nama", nama);
        params.put("jk", selectedJK);
        params.put("umur", "" + umur);
        params.put("tinggi", "" + tinggi);
        params.put("berat", "" + berat);
        params.put("kepala", "" + kepala);
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