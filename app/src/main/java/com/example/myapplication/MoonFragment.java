package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class MoonFragment extends Fragment implements AstroFragment {
    TextView latLong;
    TextView moonrise;
    TextView moonset;
    TextView newMoon;
    TextView fullMoon;
    TextView moonPhase;
    TextView calendarDay;
    String latitude;
    String longitude;
    AstroCalculator astroCalculator;
    AstroDateTime astroDateTime;
    AstroCalculator.Location location;
    SharedPreferences sp;
    boolean isReady;

    public static MoonFragment getInstance() {
        return new MoonFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        int orientation = getResources().getConfiguration().orientation;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (orientation == Configuration.ORIENTATION_PORTRAIT && isTablet) {
            view = inflater.inflate(R.layout.moon_fragment_reversed, container, false);
        } else
            view = inflater.inflate(R.layout.moon_fragment, container, false);

        latLong = view.findViewById(R.id.latLong);
        moonrise = view.findViewById(R.id.moonrise);
        moonset = view.findViewById(R.id.moonset);
        newMoon = view.findViewById(R.id.newMoon);
        moonPhase = view.findViewById(R.id.moonPhase);
        fullMoon = view.findViewById(R.id.fullMoon);
        calendarDay = view.findViewById(R.id.calendarDay);
        sp = this.getActivity().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        getSettings();
        setLatLong();
        initAstro();
        populateFragment();
        isReady = true;
        return view;

    }

    @Override
    public void populateFragment() {
        setMoonrise();
        setMoonset();
        setNewMoon();
        setMoonPhase();
        setFullMoon();
        setCalendarDay();
    }


    @Override
    public void updateData() {
        getSettings();
        initAstro();
        populateFragment();
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    private void setCalendarDay() {
        BigDecimal day = BigDecimal.valueOf(astroCalculator.getMoonInfo().getAge());
        day = day.setScale(2, RoundingMode.HALF_UP);
        calendarDay.setText(String.valueOf(day));
    }

    private void setFullMoon() {
        fullMoon.setText(getDate(astroCalculator.getMoonInfo().getNextFullMoon()));
    }

    private void setMoonPhase() {
        BigDecimal phase = BigDecimal.valueOf(astroCalculator.getMoonInfo().getIllumination() * 100);
        phase = phase.setScale(1, RoundingMode.HALF_UP);
        moonPhase.setText(phase + "%");
    }

    private void setNewMoon() {
        newMoon.setText(getDate(astroCalculator.getMoonInfo().getNextNewMoon()));
    }

    private void setMoonset() {
        moonset.setText(getFormattedTime(astroCalculator.getMoonInfo().getMoonset()));
    }

    private void setMoonrise() {
        moonrise.setText(getFormattedTime(astroCalculator.getMoonInfo().getMoonrise()));
    }

    @Override
    public void initAstro() {
        astroDateTime = new AstroDateTime();
        LocalDateTime now = LocalDateTime.now();
        astroDateTime.setYear(now.getYear());
        astroDateTime.setMonth(now.getMonthValue());
        astroDateTime.setDay(now.getDayOfMonth());
        astroDateTime.setHour(now.getHour());
        astroDateTime.setMinute(now.getMinute());
        astroDateTime.setSecond(now.getSecond());
        astroDateTime.setTimezoneOffset(2);
        location = new AstroCalculator.Location(Double.parseDouble(latitude), Double.parseDouble(longitude));
        astroCalculator = new AstroCalculator(astroDateTime, location);
    }

    @Override
    public void getSettings() {
        longitude = sp.getString("longitude", "19.457216");
        latitude = sp.getString("latitude", "51.759445");
    }

    @Override
    public void setLatLong() {
        latLong.append(" " + latitude + ", " + longitude);
    }

    private String getFormattedTime(AstroDateTime astroDateTime) {
        if(astroDateTime == null)
            return String.format("%02d:%02d:%02d", 5, 6, 6);
        int hour = astroDateTime.getHour();
        int minute = astroDateTime.getMinute();
        int second = astroDateTime.getSecond();
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    private String getDate(AstroDateTime astroDateTime) {
        int day = astroDateTime.getDay();
        int month = astroDateTime.getMonth();
        int year = astroDateTime.getYear();
        return (day + "." + month + "." + year);
    }

}
