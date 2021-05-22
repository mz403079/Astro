package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        if (settings.contains("longitude"))
            return;
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("longitude", "19.457216");
        editor.putString("latitude", "51.759445");
        editor.putInt("refreshRate", 15);
        editor.putInt("refreshRatePosition", 5);
//        editor.putBoolean("settings", false);
        editor.apply();

    }

    public void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void openAstro() {
        Intent intent = new Intent(this, ViewPager.class);
        startActivity(intent);
    }

    public void onClickSettings(View v) {
        openSettings();
    }

    public void onClickAstro(View v) {
        openAstro();
    }

}