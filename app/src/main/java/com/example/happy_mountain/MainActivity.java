package com.example.happy_mountain;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.happy_mountain.Data.LocationData;
import com.example.happy_mountain.Data.MountainData;
import com.example.happy_mountain.Fragment.ForestPointFragment;
import com.example.happy_mountain.Fragment.SettingsFragment;
import com.example.happy_mountain.Fragment.TodayFragment;
import com.example.happy_mountain.Model.LocationModel;
import com.example.happy_mountain.Model.MountainModel;
import com.example.happy_mountain.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String tag = this.getClass().toString();

    private ActivityMainBinding binding;

    LocationModel locationModel;
    LocationManager locationManager;
    MountainModel mountainModel;

    TodayFragment todayFragment = new TodayFragment();
    ForestPointFragment forestPointFragment = new ForestPointFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
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
                            .replace(R.id.frameLayout, forestPointFragment).commitAllowingStateLoss();
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("[MainActivity] LOC-M", locationManager + "");

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        } else {
            Log.d("[MainActivity]", "START LOCATION");
            startLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void startLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (location == null || l.getAccuracy() < location.getAccuracy()) {
                location = l;
            }
        }

        locationModel = new ViewModelProvider(this).get(LocationModel.class);
        Log.d("[MainActivity] LOC0", location + "");
        if (location != null) {
            Log.d("[MainActivity]", "Location Null");
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            Log.d("[MainActivity] LOC1", "위치정보 : " + provider + " 위도 : " + longitude + " 경도 : " + latitude + " 고도 : " + altitude);

            locationModel.getLocationData().postValue(new LocationData(longitude, latitude, altitude));
        }

        // 위치 정보를 원하는 시간, 거리마다 갱신
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                1,
                locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000,
                1,
                locationListener);

        mountainModel = new ViewModelProvider(this).get(MountainModel.class);

        try {
            AssetManager assetManager = this.getAssets();
            InputStream inputStream = assetManager.open("mountain_info.csv");

            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
            String[] nextLine;
            List<MountainData> items = new ArrayList<>();

            while ((nextLine = csvReader.readNext()) != null) {
                MountainData mountainData = new MountainData(nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5]);
                items.add(mountainData);
            }

            mountainModel.getMountainDataList().postValue(items);
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, todayFragment).commitAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean granted = true;
        if (requestCode == 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                }
            }
            if (!granted) {
                Toast.makeText(getApplication(), getString(R.string.permission_setting), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + getPackageName()));
                finish();
                startActivity(intent);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현 해야합니다.
            String provider = location.getProvider();  // 위치정보
            double longitude = location.getLongitude(); // 위도
            double latitude = location.getLatitude(); // 경도
            double altitude = location.getAltitude(); // 고도
            Log.d("[MainActivity] LOC", "위치정보 : " + provider + " 위도 : " + longitude + " 경도 : " + latitude + " 고도 : " + altitude);

            locationModel.getLocationData().postValue(new LocationData(longitude, latitude, altitude));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onProviderDisabled(String provider) {

        }
    };
}
