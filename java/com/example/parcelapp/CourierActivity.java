package com.example.parcelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.parcelapp.courierhome.CourierDealFragment;
import com.example.parcelapp.courierhome.CourierDispatchFragment;
import com.example.parcelapp.courierhome.CourierNotificationFragment;
import com.example.parcelapp.courierhome.CourierResolutionFragment;
import com.example.parcelapp.courierhome.CourierTabAdapter;
import com.example.parcelapp.courierhome.CourierTransactionFragment;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class CourierActivity extends AppCompatActivity {

    private final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");


    TabLayout courierTabLayout;
    ViewPager2 courierViewPager;

    public static String POSITION = "POSITION";

    CourierTabAdapter courierTabAdapter;
    SharedPreferences myPreference;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_courier);

        myPreference = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        courierTabLayout = findViewById(R.id.courierTabLayout);
        courierViewPager = findViewById(R.id.courierViewPager);

        courierTabAdapter = new CourierTabAdapter(getSupportFragmentManager(), getLifecycle());

        courierTabAdapter.addFragment(new CourierDealFragment(CourierActivity.this, myPreference));
        courierTabAdapter.addFragment(new CourierDispatchFragment(CourierActivity.this, myPreference));
        courierTabAdapter.addFragment(new CourierTransactionFragment(CourierActivity.this, myPreference));
        courierTabAdapter.addFragment(new CourierResolutionFragment(CourierActivity.this, myPreference));
        courierTabAdapter.addFragment(new CourierNotificationFragment(CourierActivity.this, myPreference));

        courierViewPager.setAdapter(courierTabAdapter);

        courierTabLayout.addTab(courierTabLayout.newTab().setText("Deals"));
        courierTabLayout.addTab(courierTabLayout.newTab().setText("Dispatches"));
        courierTabLayout.addTab(courierTabLayout.newTab().setText("Transactions"));
        courierTabLayout.addTab(courierTabLayout.newTab().setText("Resolutions"));
        courierTabLayout.addTab(courierTabLayout.newTab().setText("Notifications"));


        courierTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                courierViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        courierViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                courierTabLayout.selectTab(courierTabLayout.getTabAt(position));
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, courierTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        courierViewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
