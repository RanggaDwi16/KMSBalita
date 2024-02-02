package com.example.kmsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    String mUserJenis = "";
    String mUserPosy = "";

    Spinner _spinnerPosyandu;
    EditText etCari;
    Button _btnTambahKader;
    Button _btnStatusBalita;
    Button _btnBeratTinggiIdeal;
    Button btnPosyandu;

    Button btnActionExportPDF, btnActionExportExcel, btnLogout;

    ImageView ivCari;

    String selectedPosyandu = "_ALL_";
    String selectedPosyanduString = "-";

    ArrayList<String> posyanduList;
    HashMap<String, String> posyanduObject;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mSharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(Config.SP_USER_TOKEN, "");
        mUserJenis = mSharedPreferences.getString(Config.SP_USER_TYPE, "");
        mUserPosy = mSharedPreferences.getString(Config.SP_USER_POSY, "");
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
        btnPosyandu = findViewById(R.id.btnPosyandu);
        etCari = findViewById(R.id.etCari);
        btnActionExportPDF = findViewById(R.id.btnActionExportPDF);
        btnActionExportExcel = findViewById(R.id.btnActionExportExcel);
        btnLogout = findViewById(R.id.btnLogout);
        ivCari = findViewById(R.id.ivCari);

        etCari.setImeActionLabel("Cari", KeyEvent.KEYCODE_ENTER);
        etCari.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    renderList();
                    return true;
                }
                return false;
            }
        });
        ivCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderList();
            }
        });
        renderList();

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
        btnPosyandu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, PosyanduListActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.forceLogout(mContext);
            }
        });

        btnActionExportPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Config.BASE_URL + "/export_kmsbalita?export=pdf&keyword="+etCari.getText().toString();
                Log.d(TAG, "onClick: " + url);
                Config.browserIntent(mContext, url);
            }
        });
        btnActionExportExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Config.BASE_URL + "/export_kmsbalita?export=excel&keyword="+etCari.getText().toString();
                Log.d(TAG, "onClick: " + url);
                Config.browserIntent(mContext, url);
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
                Log.d(TAG, "onItemSelected: " + selectedPosyandu);
                //renderList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        _spinnerPuskesmas.setSelection(0);
        if(mUserJenis.equals("USER_TYPE_KADER")){
            _spinnerPuskesmas.setVisibility(View.GONE);
            selectedPosyandu = mUserPosy;
        }

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
                                selectedPosyanduString = selected;
                                //renderList();
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
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            // Jika ada lebih dari satu elemen, mulai dari indeks ke-1 (indeks pertama adalah header)
            for (int i = 1; i < childCount; i++) {
                // Hapus setiap elemen kecuali header
                tableLayout.removeViewAt(1);
            }
        }
        String keyword = etCari.getText().toString().trim();
        String url = Config.BASE_URL + "/tes_get";
        Map<String, String> params = new HashMap<>();
        params.put("keyword", keyword);
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
                            View view = layoutInflater.inflate(R.layout.itemrow_cek, null);
                            JSONObject _tes = payload.optJSONObject(i);
                            TextView tvNama = view.findViewById(R.id.tvNama);
                            TextView tvJK = view.findViewById(R.id.tvJK);
                            TextView tvUmur = view.findViewById(R.id.tvUmur);
                            TextView tvTinggi = view.findViewById(R.id.tvTinggi);
                            TextView tvBerat = view.findViewById(R.id.tvBerat);
                            TextView tvKepala = view.findViewById(R.id.tvKepala);
                            TextView tvPosyandu = view.findViewById(R.id.tvPX);

                            String jk = (_tes.optString("TES_JK").equals("GENDER_MALE")) ? "L" : "P";
                            int umur = _tes.optInt("TES_UMUR");
                            Double tinggi = _tes.optDouble("TES_TINGGI");
                            Double berat = _tes.optDouble("TES_BERAT");
                            Double kepala = _tes.optDouble("TES_KEPALA");
                            String rTinggi = _tes.optString("TES_HASIL_TINGGI");
                            String rBerat = _tes.optString("TES_HASIL_BERAT");
                            String rKepala = _tes.optString("TES_HASIL_KEPALA");
                            tvNama.setText("" + _tes.optString("TES_NAMA"));
                            tvJK.setText("" + jk);
                            tvUmur.setText("" + umur);
                            tvTinggi.setText("" + tinggi + "\n" + rTinggi);
                            tvBerat.setText("" + berat + "\n" + rBerat);
                            tvKepala.setText("" + kepala + "\n" + rKepala);
                            tvPosyandu.setText(_tes.optString("POSY_NAMA"));

                            tableLayout.addView(view);
                        }
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


    private void validateRole(){
        String role = mSharedPreferences.getString(Config.SP_USER_TYPE, "");
        if(role.equals("USER_TYPE_ADMIN")){
            return;
        }

        Button btnTambahKader = findViewById(R.id.btnTambahKader);
        TextView textView13 = findViewById(R.id.textView13);
        LinearLayout divFilter = findViewById(R.id.divFilter);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        LinearLayout divExport = findViewById(R.id.btnExportPDF);
        LinearLayout divKaderPosy = findViewById(R.id.divKaderPosy);

        divKaderPosy.setVisibility(View.INVISIBLE);
        textView13.setVisibility(View.INVISIBLE);
        divFilter.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.INVISIBLE);
        divExport.setVisibility(View.INVISIBLE);
    }


}