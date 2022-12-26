package com.example.happy_mountain;

import static android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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

import com.example.happy_mountain.databinding.ActivityMainBinding;
import com.example.happy_mountain.fragment.ForestPointFragment;
import com.example.happy_mountain.fragment.LocationFragment;
import com.example.happy_mountain.fragment.SettingsFragment;
import com.example.happy_mountain.item.LocationItem;
import com.example.happy_mountain.model.LocationModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    LocationModel locationModel;
    LocationManager locationManager;

    LocationFragment todayFragment = new LocationFragment();
    ForestPointFragment forestPointFragment = new ForestPointFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        BottomNavigationView bottomMenu = binding.bottomMenu;

        // 해쉬 키 구하기
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), GET_SIGNING_CERTIFICATES);
                Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();

                for (int i = 0; i < signatures.length; i++) {
                    MessageDigest message = MessageDigest.getInstance("SHA");
                    message.update(signatures[i].toByteArray());

                    String hash = Base64.getEncoder().encodeToString(message.digest());
                    Log.d("Main", "Hash : " + hash);
                }
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        bottomMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottomMenu1:
                    Log.d("[Main]", "BottomMenu1 : Home");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, todayFragment).commitAllowingStateLoss();
                    return true;
                case R.id.bottomMenu2:
                    Log.d("[Main]", "BottomMenu2 : Control");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, forestPointFragment).commitAllowingStateLoss();
                    return true;
                case R.id.bottomMenu3:
                    Log.d("[Main]", "BottomMenu3 : Settings");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, settingsFragment).commitAllowingStateLoss();
                    return true;
                default:
                    return false;
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("[Main] LOC-M", locationManager + "");

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
            Log.d("[Main]", "START LOCATION");
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
        Log.d("[Main] LOC0", location + "");
        if (location != null) {
            Log.d("[Main]", "Location Null");
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            Log.d("[Main] LOC1", "위치정보 : " + provider + " 위도 : " + longitude + " 경도 : " + latitude);

            locationModel.getLocationData().postValue(new LocationItem(longitude, latitude));
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);

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
                Toast.makeText(getApplication(), getString(R.string.permissionSetting), Toast.LENGTH_LONG).show();
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
            Log.d("[Main] LOC", "위치정보 : " + provider + " 위도 : " + longitude + " 경도 : " + latitude);

            locationModel.getLocationData().postValue(new LocationItem(longitude, latitude));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onProviderDisabled(String provider) {

        }
    };
}
