package com.example.warm_weather;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.warm_weather.Fragment.DiseaseFragment;
import com.example.warm_weather.Fragment.SettingsFragment;
import com.example.warm_weather.Fragment.TodayFragment;
import com.example.warm_weather.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final String tag = this.getClass().toString();

    private ActivityMainBinding binding;

    Toolbar toolbar;

    TodayFragment todayFragment = new TodayFragment();
    DiseaseFragment diseaseFragment = new DiseaseFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, todayFragment).commitAllowingStateLoss();
        BottomNavigationView bottomMenu = binding.bottomMenu;

        bottomMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottomMenu1:
                    Log.d("[" + tag + "]", "BottomMenu1 : Home");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, todayFragment).commitAllowingStateLoss();
                    return true;
                case R.id.bottomMenu2:
                    Log.d("[" + tag + "]", "BottomMenu2 : Control");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, diseaseFragment).commitAllowingStateLoss();
                    return true;
                case R.id.bottomMenu3:
                    Log.d("[" + tag + "]", "BottomMenu3 : Settings");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, settingsFragment).commitAllowingStateLoss();
                    return true;
                default:
                    return false;
            }
        });
    }
}
