package com.example.kmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.kmsapp.helper.ActionCallback;
import com.example.kmsapp.helper.Config;

public class StatusBalita extends AppCompatActivity {
    String TAG = Config.DEBUG_TAG;
    boolean debugMode = Config.DEBUG_MODE;
    SharedPreferences mSharedPreferences;
    StatusBalita mContext;
    String mUserToken = "";

    String posyandu = "";

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
        posyandu = getIntent().getExtras().getString("POSYANDU");
        bind(new ActionCallback() {
            @Override
            public void onActionDone() {
            }
        });
    }

    private void bind(ActionCallback callback){
//        _btnTambahKader = (Button) findViewById(R.id.btnTambahKader);
//        _btnStatusBalita = (Button) findViewById(R.id.btnStatusBalita);
//        _btnBeratTinggiIdeal = (Button) findViewById(R.id.btnBeratTinggiIdeal);
//        _spinnerPosyandu = (Spinner) findViewById(R.id.posyandu);
//
//        _btnTambahKader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomePage.this, TambahKader.class);
//                startActivity(intent);
//            }
//        });
//
//        _btnStatusBalita.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showOptionDialog();
//
//            }
//        });
//
//        _btnBeratTinggiIdeal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomePage.this, BeratTinggiIdeal.class);
//                startActivity(intent);
//            }
//        });

        callback.onActionDone();
    }

}