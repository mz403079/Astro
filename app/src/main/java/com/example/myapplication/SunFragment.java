package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.astrocalculator.AstroCalculator.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SunFragment extends Fragment implements AstroFragment {
    TextView latLong;
    TextView sunriseTime;
    TextView sunriseAzimuth;
    TextView sunsetTime;
    TextView sunsetAzimuth;
    TextView dusk;
    TextView twilight;
    String latitude;
    String longitude;
    AstroCalculator astroCalculator;
    AstroDateTime astroDateTime;
    Location location;
    SharedPreferences sp;
    boolean isReady;

    public static SunFragment getInstance() {
        return new SunFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sun_fragment, container, false);
        latLong = view.findViewById(R.id.latLong);
        sunriseTime = view.findViewById(R.id.sunriseTime);
        sunriseAzimuth = view.findViewById(R.id.sunriseAzimuth);
        sunsetTime = view.findViewById(R.id.sunsetTime);
        sunsetAzimuth = view.findViewById(R.id.sunsetAzimuth);
        dusk = view.findViewById(R.id.dusk);
        twilight = view.findViewById(R.id.twilight);
        sp = this.getActivity().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        getSettings();
        setLatLong();
        initAstro();
        populateFragment();
        isReady = true;
        return view;
    }

    @Override
    public void initAstro() {
        astroDateTime = new AstroDateTime();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        astroDateTime.setYear(now.getYear());
        astroDateTime.setMonth(now.getMonthValue());
        astroDateTime.setDay(now.getDayOfMonth());
        astroDateTime.setHour(now.getHour());
        astroDateTime.setMinute(now.getMinute());
        astroDateTime.setSecond(now.getSecond());
        astroDateTime.setTimezoneOffset(2);
        location = new Location(Double.parseDouble(latitude), Double.parseDouble(longitude));
        astroCalculator = new AstroCalculator(astroDateTime, location);
    }

    @Override
    public void getSettings() {

        longitude = sp.getString("longitude", "19.457216");
        latitude = sp.getString("latitude", "51.759445");
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void populateFragment() {
        setSunriseTime();
        setSunriseAzimuth();
        setSunsetTime();
        setSunsetAzimuth();
        setDusk();
        setTwilight();
    }

    @Override
    public void updateData() {
        getSettings();
        initAstro();
        populateFragment();
    }

    @Override
    public void setLatLong() {
        latLong.append(" " + latitude + ", " + longitude);
    }

    private String getFormattedTime(AstroDateTime astroDateTime) {
        int hour = astroDateTime.getHour();
        int minute = astroDateTime.getMinute();
        int second = astroDateTime.getSecond();
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    private void setSunriseTime() {
        sunriseTime.setText(getFormattedTime(astroCalculator.getSunInfo().getSunrise()));
    }

    private void setSunriseAzimuth() {
        BigDecimal azimuth = BigDecimal.valueOf(astroCalculator.getSunInfo().getAzimuthRise());
        azimuth = azimuth.setScale(2, RoundingMode.HALF_UP);
        sunriseAzimuth.setText(azimuth + "°");
    }

    private void setSunsetTime() {
        sunsetTime.setText(getFormattedTime(astroCalculator.getSunInfo().getSunset()));
    }

    private void setSunsetAzimuth() {
        BigDecimal azimuth = BigDecimal.valueOf(astroCalculator.getSunInfo().getAzimuthSet());
        azimuth = azimuth.setScale(2, RoundingMode.HALF_UP);
        sunsetAzimuth.setText(azimuth + "°");
    }

    private void setDusk() {
        dusk.setText(getFormattedTime(astroCalculator.getSunInfo().getTwilightMorning()));
    }

    private void setTwilight() {
        twilight.setText(getFormattedTime(astroCalculator.getSunInfo().getTwilightEvening()));
    }
}
