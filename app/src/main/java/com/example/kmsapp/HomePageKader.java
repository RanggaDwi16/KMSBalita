package com.example.kmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomePageKader extends AppCompatActivity {

    Button btnStatusBalita;
    Button btnBeratTinggiIdeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_kader);

        btnStatusBalita = (Button) findViewById(R.id.btnStatusBalita);
        btnBeratTinggiIdeal = (Button) findViewById(R.id.btnBeratTinggiIdeal);

        btnStatusBalita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageKader.this, StatusBalita.class);
                startActivity(intent);
            }
        });

        btnBeratTinggiIdeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageKader.this, BeratTinggiIdeal.class);
                startActivity(intent);
            }
        });
    }
}