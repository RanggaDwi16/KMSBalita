package com.example.kmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kmsapp.helper.ActionCallback;
import com.example.kmsapp.helper.Config;

import java.util.ArrayList;
import java.util.HashMap;

public class BeratTinggiIdeal extends AppCompatActivity {
    String TAG = Config.DEBUG_TAG;
    boolean debugMode = Config.DEBUG_MODE;
    SharedPreferences mSharedPreferences;
    BeratTinggiIdeal mContext;
    String mUserToken = "";

    EditText etUmur;
    TextView tvNormalTinggi, tvNormalBerat, tvNormalKepala;
    Spinner spnJK;
    String selectedJK;

    TextView tvTinggiRef, tvBeratRef, tvKepalaRef;
    ImageView ivTinggiRef, ivBeratRef, ivKepalaRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berat_tinggi_ideal);

        mSharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(Config.SP_USER_TOKEN, "");
        mContext = BeratTinggiIdeal.this;
        if(mUserToken.equals("")){
            Config.forceLogout(mContext);
        }
        bind(new ActionCallback() {
            @Override
            public void onActionDone() {
                setupTrigger();
            }
        });
    }


    private void bind(ActionCallback callback){
        etUmur = findViewById(R.id.etUmur);
        tvNormalTinggi = findViewById(R.id.tvNormalTinggi);
        tvNormalBerat = findViewById(R.id.tvNormalBerat);
        tvNormalKepala = findViewById(R.id.tvNormalKepala);
        spnJK = findViewById(R.id.spnJK);
        tvTinggiRef = findViewById(R.id.tvTinggiRef);
        tvBeratRef = findViewById(R.id.tvBeratRef);
        tvKepalaRef = findViewById(R.id.tvKepalaRef);
        ivTinggiRef = findViewById(R.id.ivTinggiRef);
        ivBeratRef = findViewById(R.id.ivBeratRef);
        ivKepalaRef = findViewById(R.id.ivKepalaRef);

        ArrayList<String> jkList = new ArrayList<>();
        jkList.add("Laki-laki");
        jkList.add("Perempuan");
        HashMap<String, String> jkObject = new HashMap<>();
        jkObject.put("Laki-laki", "GENDER_MALE");
        jkObject.put("Perempuan", "GENDER_FEMALE");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, jkList);
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
        tvNormalTinggi.setText("-");
        tvNormalBerat.setText("-");
        tvNormalKepala.setText("-");

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
        if(selectedJK.equals("GENDER_MALE")){
            ivTinggiRef.setBackgroundResource(R.drawable.tinggi_laki);
            ivBeratRef.setBackgroundResource(R.drawable.berat_laki);
            ivKepalaRef.setBackgroundResource(R.drawable.lingkar_laki);
            tvTinggiRef.setText("Tinggi badan menurut umur (laki-laki)");
            tvBeratRef.setText("Berat badan menurut umur (laki-laki)");
            tvKepalaRef.setText("Lingkar kepala menurut umur (laki-laki)");
        }
        if(selectedJK.equals("GENDER_FEMALE")){
            ivTinggiRef.setBackgroundResource(R.drawable.tinggi_perempuan);
            ivBeratRef.setBackgroundResource(R.drawable.berat_perempuan);
            ivKepalaRef.setBackgroundResource(R.drawable.lingkar_perempuan);
            tvTinggiRef.setText("Tinggi badan menurut umur (perempuan)");
            tvBeratRef.setText("Berat badan menurut umur (perempuan)");
            tvKepalaRef.setText("Lingkar kepala menurut umur (perempuan)");
        }

        String v4 = etUmur.getText().toString();
        if (
               v4.isEmpty() || v4.equals("0") || v4.startsWith(".")
        ){
            tvNormalTinggi.setText("-");
            tvNormalBerat.setText("-");
            tvNormalKepala.setText("-");
            return;
        }

        Double umur = Double.valueOf(etUmur.getText().toString());
        String[] rTinggi = Config.getBalitaStatus(selectedJK + " TINGGI " + umur, null).get(1).split(" ");
        String[] rBerat = Config.getBalitaStatus(selectedJK + " BERAT " + umur, null).get(1).split(" ");
        String[] rKepala = Config.getBalitaStatus(selectedJK + " KEPALA " + umur, null).get(1).split(" ");
        tvNormalTinggi.setText(rTinggi[0] + " cm - " + rTinggi[1] + " cm");
        tvNormalBerat.setText(rBerat[0] + " kg - " + rBerat[1] + " kg");
        tvNormalKepala.setText(rKepala[0] + " cm - " + rKepala[1] + " cm");
    }


}