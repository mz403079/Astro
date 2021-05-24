package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ViewPager extends AppCompatActivity {
    ViewPagerAdapter adapter;
    Handler refreshHandler = new Handler();
    Handler settingsHandler = new Handler();
    List<AstroFragment> fragments = new ArrayList<>();
    int refreshRate;
    FragmentManager fragmentManager;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        sp = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        refreshRate = sp.getInt("refreshRate", 15);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            fragments.add((AstroFragment) fragmentManager.findFragmentById(R.id.frag1));
            fragments.add((AstroFragment) fragmentManager.findFragmentById(R.id.frag2));
        } else {

            TabLayout tabLayout = findViewById(R.id.tabs);
            ViewPager2 viewPager2 = findViewById(R.id.view_pager);

            adapter = new ViewPagerAdapter(this);

            adapter.addFragment(SunFragment.getInstance(), "SUN");
            adapter.addFragment(MoonFragment.getInstance(), "MOON");
            adapter.addFragment(BaseWeatherFragment.getInstance(), "WEATHER");
//            adapter.addFragment(MoonFragment.getInstance(), "MOON");
//            adapter.addFragment(SunFragment.getInstance(), "SUN");
//            adapter.addFragment(MoonFragment.getInstance(), "MOON");
            viewPager2.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager2,
                    new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override
                        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                            tab.setText(adapter.getName(position));
                        }
                    }).attach();
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if(adapter.getFragment(i) instanceof AstroFragment)
                    fragments.add((AstroFragment) adapter.getFragment(i));
            }
        }
        updateData();
//        updateSettings();
    }

    @Override
    protected void onStop() {
        refreshHandler.removeCallbacksAndMessages(null);
        settingsHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    public void updateData() {
        final Runnable thread = new Runnable() {
            @Override
            public void run() {
                refreshHandler.postDelayed(this, 1000 * refreshRate);   //60000, 1000
           //     System.out.println(refreshRate);
                for (AstroFragment af : fragments) {

                    if (af.isReady())
                        af.updateData();

                }
            }
        };
        refreshHandler.postDelayed(thread, 10);
    }

    public void updateSettings() {
        final Runnable thread = new Runnable() {
            @Override
            public void run() {
                settingsHandler.postDelayed(this, 1000);
                if (sp.getBoolean("settings", false)) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("settings", false);
                    editor.apply();
                    refreshRate = sp.getInt("refreshRate", 15);
                    for (AstroFragment af : fragments) {
                        if (af.isReady())
                            af.setLatLong();
                    }
                }
            }
        };
        settingsHandler.postDelayed(thread, 10);
    }
}
