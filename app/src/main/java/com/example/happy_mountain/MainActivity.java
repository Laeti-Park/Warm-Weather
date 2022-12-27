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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.happy_mountain.adapter.WarningRateAdapter;
import com.example.happy_mountain.databinding.ActivityMainBinding;
import com.example.happy_mountain.fragment.WarningRateFragment;
import com.example.happy_mountain.fragment.LocationFragment;
import com.example.happy_mountain.item.LocationItem;
import com.example.happy_mountain.item.WarningRateItem;
import com.example.happy_mountain.model.LocationModel;
import com.example.happy_mountain.model.WarningRateModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private LocationModel locationModel;
    WarningRateModel warningRateModel;
    private LocationManager locationManager;

    private LocationFragment locationFragment;
    private WarningRateFragment warningRateFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.happy_mountain.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        BottomNavigationView bottomMenu = binding.bottomMenu;

        // 해쉬 키 구하기
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), GET_SIGNING_CERTIFICATES);
                Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();

                for (Signature signature : signatures) {
                    MessageDigest message = MessageDigest.getInstance("SHA");
                    message.update(signature.toByteArray());

                    String hash = Base64.getEncoder().encodeToString(message.digest());
                    Log.d("Main", "Hash : " + hash);
                }
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        locationFragment = new LocationFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, locationFragment).commitAllowingStateLoss();

        bottomMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottomMenu1:
                    Log.d("Main", "BottomMenu1");
                    if (locationFragment != null) {
                        Log.d("Main", "BottomMenu2 : hide 1");
                        getSupportFragmentManager().beginTransaction().show(locationFragment).commit();
                    }
                    if (warningRateFragment != null) {
                        Log.d("Main", "BottomMenu2 : show 2");
                        getSupportFragmentManager().beginTransaction().hide(warningRateFragment).commit();
                    }
                    return true;
                case R.id.bottomMenu2:
                    Log.d("Main", "BottomMenu2");
                    if (warningRateFragment == null) {
                        warningRateFragment = new WarningRateFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, warningRateFragment).commit();
                    }
                    if (locationFragment != null) {
                        Log.d("Main", "BottomMenu2 : hide 1");
                        getSupportFragmentManager().beginTransaction().hide(locationFragment).commit();
                    }
                    if (warningRateFragment != null) {
                        Log.d("Main", "BottomMenu2 : show 2");
                        getSupportFragmentManager().beginTransaction().show(warningRateFragment).commit();
                    }
                    return true;
                default:
                    return false;
            }
        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("Main", locationManager + "");

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
            startLocation();
            loadMetadata();
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
        warningRateModel = new ViewModelProvider(this).get(WarningRateModel.class);
        Log.d("Main", location + "");
        if (location != null) {
            Log.d("Main", "Location Null");
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            Log.d("Main LOC1", "위치정보 : " + provider + " 위도 : " + longitude + " 경도 : " + latitude);

            locationModel.getLocationData().postValue(new LocationItem(longitude, latitude));
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
    }

    public void loadMetadata() {
        new Thread(() -> {
            try {
                String urlBuilder = "https://www.data.go.kr/catalog/15092027/fileData.json";
                Log.i("[WarningRate]", "URL: " + urlBuilder);

                URL url = new URL(urlBuilder);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                Log.i("[WarningRate]", "Response code: " + conn.getResponseCode());

                BufferedReader bufferedReader;
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                conn.disconnect();
                jsonParsing(stringBuilder.toString());
            } catch (IOException e) {
                Log.i("[WarningRate] ERROR : ", e.toString());
            }
        }).start();
    }

    public void jsonParsing(String jsonString) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
            DateFormat dateViewFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
            long now = System.currentTimeMillis();
            Date date = new Date(now);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, -2);
            Date checkDate = cal.getTime();

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray distributionArray = jsonObject.getJSONArray("distribution");
            JSONObject distributionObject = distributionArray.getJSONObject(0);
            String contentUrl = distributionObject.getString("contentUrl");
            Log.i("WarningRate", contentUrl);

            URL url = new URL(contentUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream, "EUC-KR"));
            List<String[]> contents = reader.readAll();
            List<WarningRateItem> warningRateItems = new ArrayList<>();
            for (int i = contents.size() - 1; i >= contents.size() / 3; i--) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Date compDate = dateFormat.parse(contents.get(i)[0]);
                    if (Objects.requireNonNull(compDate).compareTo(checkDate) > 0) {
                        Log.i("WarningRate", "content : " + Arrays.toString(contents.get(i)));
                        warningRateItems.add(new WarningRateItem(contents.get(i)[0], contents.get(i)[1],
                                contents.get(i)[1] + " " + contents.get(i)[2] + " " + contents.get(i)[3],
                                contents.get(i)[4], contents.get(i)[5], contents.get(i)[6]));
                    }
                }
            }
            Log.i("Main", "item count : " + warningRateItems.size());
            String dateText = dateViewFormat.format(checkDate) + " ~ " + dateViewFormat.format(date);

            warningRateModel.getWarningRateList().postValue(warningRateItems);
            warningRateModel.getDateInfo().postValue(dateText);

        } catch (JSONException | IOException | CsvException | ParseException e) {
            e.printStackTrace();
        }
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
            Log.d("Main LOC", "위치정보 : " + provider + " 위도 : " + longitude + " 경도 : " + latitude);

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
