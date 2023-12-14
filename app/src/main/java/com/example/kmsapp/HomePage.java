package com.example.kmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    String TAG = Config.DEBUG_TAG;
    boolean debugMode = Config.DEBUG_MODE;
    SharedPreferences mSharedPreferences;
    HomePage mContext;
    String mUserToken = "";

    Spinner _spinnerPosyandu;
    Button _btnTambahKader;
    Button _btnStatusBalita;
    Button _btnBeratTinggiIdeal;

    String selectedPosyandu = "_ALL_";

    ArrayList<String> posyanduList;
    HashMap<String, String> posyanduObject;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mSharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(Config.SP_USER_TOKEN, "");
        mContext = HomePage.this;
        if(mUserToken.equals("")){
            Config.forceLogout(mContext);
        }

        bind(new ActionCallback() {
            @Override
            public void onActionDone() {
                setupSpinner();
                validateRole();
            }
        });
    }


    private void bind(ActionCallback callback){
        _btnTambahKader = (Button) findViewById(R.id.btnTambahKader);
        _btnStatusBalita = (Button) findViewById(R.id.btnStatusBalita);
        _btnBeratTinggiIdeal = (Button) findViewById(R.id.btnBeratTinggiIdeal);
        _spinnerPosyandu = (Spinner) findViewById(R.id.posyandu);

        _btnTambahKader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, TambahKader.class);
                startActivity(intent);
            }
        });

        _btnStatusBalita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog();

            }
        });

        _btnBeratTinggiIdeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, BeratTinggiIdeal.class);
                startActivity(intent);
            }
        });

        callback.onActionDone();
    }


    private void showOptionDialog() {
        // Inflate the custom layout for the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_pop_up, null);
        dialogBuilder.setView(dialogView);

        Spinner _spinnerPuskesmas = (Spinner) dialogView.findViewById(R.id.spinnerPuskesmas);
        TextView datePicker = dialogView.findViewById(R.id.datePicker);

        final String[] tanggal = {""};
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.showDatePickerDialog(new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tanggal[0] = Config.pad(year) + "-" + Config.pad(month + 1) + "-" + Config.pad(dayOfMonth);
                        datePicker.setText(Config.tglIndo(tanggal[0], "LONG"));
                    }
                }, mContext);
            }
        });

        _spinnerPuskesmas.setAdapter(adapter);
        _spinnerPuskesmas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = _spinnerPuskesmas.getSelectedItem().toString();
                selectedPosyandu = posyanduObject.get(selected);
                renderList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        _spinnerPuskesmas.setSelection(0);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedPosyandu.equals("_ALL_") || tanggal[0].equals("")){
                    Toast.makeText(mContext, "Mohon pilih posyandu dan tanggal", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(HomePage.this, StatusBalita.class);
                intent.putExtra("POSYANDU", selectedPosyandu);
                intent.putExtra("TANGGAL", tanggal[0]);
                startActivity(intent);
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Cancel button click

                // Dismiss the dialog (optional)
                dialog.dismiss();
            }
        });

        // Show the AlertDialog
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    private void setupSpinner(){
        String url = Config.BASE_URL + "/posyandu_get";
        Map<String, String> params = new HashMap<>();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseString) {
                Log.d(TAG, "setupSpinner.onResponse : " + responseString);
                JSONObject response = null;
                try {
                    response = new JSONObject(responseString);
                    final String status = response.optString(Config.RESPONSE_STATUS_FIELD);
                    final String message = response.optString(Config.RESPONSE_MESSAGE_FIELD);
                    if(status.equalsIgnoreCase(Config.RESPONSE_STATUS_VALUE_SUCCESS)) {
                        JSONArray payload = response.optJSONArray(Config.RESPONSE_PAYLOAD_FIELD);
                        posyanduList = new ArrayList<>();
                        posyanduObject = new HashMap<>();
                        posyanduList.add("SEMUA");
                        posyanduObject.put("SEMUA", "_ALL_");
                        for (int i = 0; i < payload.length(); i++) {
                            JSONObject _posyandu = payload.optJSONObject(i);
                            posyanduList.add(_posyandu.optString("POSY_NAMA"));
                            posyanduObject.put(_posyandu.optString("POSY_NAMA"), _posyandu.optString("POSY_ID"));
                        }

                        adapter = new ArrayAdapter<>(HomePage.this, android.R.layout.simple_spinner_item, posyanduList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        _spinnerPosyandu.setAdapter(adapter);
                        _spinnerPosyandu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String selected = _spinnerPosyandu.getSelectedItem().toString();
                                selectedPosyandu = posyanduObject.get(selected);
                                renderList();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                        _spinnerPosyandu.setSelection(0);
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
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.posyandu, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        _spinnerPosyandu.setAdapter(adapter);
//
//        _spinnerPosyandu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }


    private void renderList(){

    }


    private void validateRole(){
        String role = mSharedPreferences.getString(Config.SP_USER_TYPE, "");
        if(role.equals("USER_TYPE_ADMIN")){
            return;
        }

        Button btnTambahKader = findViewById(R.id.btnTambahKader);
        TextView textView13 = findViewById(R.id.textView13);
        Spinner posyandu = findViewById(R.id.posyandu);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        Button btnExportPDF = findViewById(R.id.btnExportPDF);

        btnTambahKader.setVisibility(View.INVISIBLE);
        textView13.setVisibility(View.INVISIBLE);
        posyandu.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.INVISIBLE);
        btnExportPDF.setVisibility(View.INVISIBLE);
    }


}