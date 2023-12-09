package com.example.kmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

public class HomePageAdmin extends AppCompatActivity {

    Spinner _spinnerPosyandu;
    Button _btnTambahKader;
    Button _btnStatusBalita;
    Button _btnBeratTinggiIdeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_admin);

        _btnTambahKader = (Button) findViewById(R.id.btnTambahKader);
        _btnStatusBalita = (Button) findViewById(R.id.btnStatusBalita);
        _btnBeratTinggiIdeal = (Button) findViewById(R.id.btnBeratTinggiIdeal);
        _spinnerPosyandu = (Spinner) findViewById(R.id.posyandu);

        _btnTambahKader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageAdmin.this, TambahKader.class);
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
                Intent intent = new Intent(HomePageAdmin.this, BeratTinggiIdeal.class);
                startActivity(intent);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.posyandu, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        _spinnerPosyandu.setAdapter(adapter);

        _spinnerPosyandu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Code to handle item selection
                String selectedRole = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Code to handle no item selected
            }
        });
    }

    private void showOptionDialog() {
        // Inflate the custom layout for the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_pop_up, null);
        dialogBuilder.setView(dialogView);
        Spinner _spinnerPuskesmas = (Spinner) dialogView.findViewById(R.id.spinnerPuskesmas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.puskesmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        _spinnerPuskesmas.setAdapter(adapter);

        _spinnerPuskesmas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Code to handle item selection
                String selectedRole = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Code to handle no item selected
            }
        });

        // Find views in the custom dialog layout
        // Example: TextView textView = dialogView.findViewById(R.id.textView);

        // Set up any other customization or listeners for your dialog views here

        // Set positive and negative buttons
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HomePageAdmin.this, StatusBalita.class);
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

}