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
import com.example.myapplication.model.ConsolidatedWeather;
import com.example.myapplication.model.Forecast;
import com.example.myapplication.model.Location;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView tvLongitude;
    TextView tvLatitude;
    Spinner spinner;
    boolean showDefault = true;
    int refreshRate;
    int refreshRatePosition;
    private GitHubService apiService;
    public static final String BASE_URL = "https://www.metaweather.com/api/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        apiService = retrofit.create(GitHubService.class);
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
        if (savedInstanceState != null) {
            showDefault = (savedInstanceState.getBoolean("default", false));
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
        Call<List<Location>> call = apiService.getWoeid();
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                System.out.println("DONE");
                int statusCode = response.code();
                List<Location> user = response.body();

                Call<Forecast> forecastCall = apiService.getForecast(user.get(0).getWoeid());
                forecastCall.enqueue(new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                        System.out.println("DONE");
                        Forecast forecast = response.body();

                        for (ConsolidatedWeather cw : forecast.getConsolidatedWeather()) {
                            System.out.println(cw.getApplicableDate() + cw.getTheTemp());

                        }
                    }

                    @Override
                    public void onFailure(Call<Forecast> call, Throwable t) {
                        System.out.println(t.getMessage());

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                System.out.println(t.getMessage());

            }
        });
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
