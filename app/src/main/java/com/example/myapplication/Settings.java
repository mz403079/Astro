package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.util.Calendar;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView tvLongitude;
    TextView tvLatitude;
    Spinner spinner;
    boolean showDefault = true;
    int refreshRate;
    int refreshRatePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        tvLatitude = findViewById(R.id.latitude);
        tvLongitude = findViewById(R.id.longitude);
        spinner = findViewById(R.id.spinner);
        SharedPreferences settings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        String longitude = settings.getString("longitude", "19.457216");
        String latitude = settings.getString("latitude", "31.759445");
        refreshRate = settings.getInt("refreshRate", 15);
        refreshRatePosition = settings.getInt("refreshRatePosition", 5);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(refreshRatePosition);
        spinner.setOnItemSelectedListener(this);
        tvLatitude.setText(latitude);
        tvLongitude.setText(longitude);
        if(savedInstanceState != null) {
            showDefault = (savedInstanceState.getBoolean("default",false));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("default", false);
        super.onSaveInstanceState(outState);
    }

    //returns true if user input is correct
    public boolean validateInput() {
        double latitude = Double.parseDouble(tvLatitude.getText().toString());
        double longitude = Double.parseDouble(tvLongitude.getText().toString());
        if (latitude <= -90 || latitude >= 90 || longitude <= -180 || longitude >= 180)
            return false;
        return true;
    }

    public void onClickSave(View v) {
        if (!validateInput()) {
            Toast.makeText(getApplicationContext(), "Wrong input!", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences settings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("refreshRatePosition", refreshRatePosition);
        editor.putInt("refreshRate", refreshRate);
        editor.putString("longitude", tvLongitude.getText().toString());
        editor.putString("latitude", tvLatitude.getText().toString());
//        editor.putBoolean("settings",true);
        editor.apply();
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getChildCount() == 0)
            return;
        if (showDefault) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            ((TextView) parent.getChildAt(0)).setTextSize(getResources().getDimension(R.dimen.textSizeMini));
            showDefault = false;
            return;
        }
        refreshRate = Integer.parseInt(parent.getItemAtPosition(position).toString());
        refreshRatePosition = position;
        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) parent.getChildAt(0)).setTextSize(getResources().getDimension(R.dimen.textSizeMini));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
