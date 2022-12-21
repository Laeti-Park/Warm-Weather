package com.example.happy_mountain.Fragment;

import static com.naver.maps.map.NaverMap.LAYER_GROUP_MOUNTAIN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.happy_mountain.BuildConfig;
import com.example.happy_mountain.Data.MountainData;
import com.example.happy_mountain.Data.WeatherData;
import com.example.happy_mountain.Model.LocationModel;
import com.example.happy_mountain.Model.MountainModel;
import com.example.happy_mountain.Model.WeatherModel;
import com.example.happy_mountain.databinding.FragmentTodayBinding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TodayFragment extends Fragment implements OnMapReadyCallback {
    private final String tag = this.getClass().toString();
    private final String baseURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private final String serviceKey = BuildConfig.API_KEY;

    private final DateFormat nowDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
    private final DateFormat nowHourFormat = new SimpleDateFormat("HH", Locale.KOREAN);
    private String paramDate;
    private String paramHour;

    private LocationModel locationModel;
    private double longitude;
    private double latitude;
    private double altitude;
    LatLng latLng;

    private WeatherModel weatherModel;
    private List<WeatherData> weatherDataList = new ArrayList<>();

    private MountainModel mountainModel;

    private TextView longitudeView;
    private TextView latitudeView;
    private MapView mapView;
    private static NaverMap naverMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    @SuppressLint("DefaultLocale")
    public TodayFragment() {
        TimeZone timeZone;
        timeZone = TimeZone.getTimeZone("Asia/Seoul");
        nowDateFormat.setTimeZone(timeZone);
        nowHourFormat.setTimeZone(timeZone);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTodayBinding binding = FragmentTodayBinding.inflate(inflater, container, false);
        longitudeView = binding.longitudeView;
        latitudeView = binding.latitudeView;
        mapView = binding.mapView;

        Log.d("[TodayFragment] view", latitudeView.getText() + " " + longitudeView.getText());

        locationModel = new ViewModelProvider(requireActivity()).get(LocationModel.class);
        weatherModel = new ViewModelProvider(requireActivity()).get(WeatherModel.class);
        mountainModel = new ViewModelProvider(requireActivity()).get(MountainModel.class);
        locationModel.getLocationData().observe(requireActivity(), locationItem -> {
            long now = System.currentTimeMillis();
            Date date = new Date(now);

            paramDate = updateDateFormat(nowHourFormat.format(date), date);
            paramHour = updateHourFormat(nowHourFormat.format(date));

            latitude = locationItem.getLatitude();
            longitude = locationItem.getLongitude();

            latLng = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
            Log.i("아저씨", latLng + "");

            latitudeView.setText(String.valueOf((int) latitude));
            longitudeView.setText(String.valueOf((int) longitude));

            updateLocation();
        });
        mapView.getMapAsync(this);

        return binding.getRoot();
    }

    private String updateDateFormat(String hour, Date date) {
        if (Integer.parseInt(hour) - (1 + Integer.parseInt(hour) % 3) < 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);

            return nowDateFormat.format(cal.getTime());
        } else {
            return nowDateFormat.format(date);
        }
    }

    @SuppressLint("DefaultLocale")
    public String updateHourFormat(String hour) {
        if (Integer.parseInt(hour) % 3 != 2) {
            int hourComp = Integer.parseInt(hour) - (1 + Integer.parseInt(hour) % 3);
            if (hourComp < 0) {
                hourComp = 24 + hourComp;
            }
            return String.format("%02d", hourComp) + "00";
        } else {
            return hour + "00";
        }
    }

    public void updateLocation() {
        loadWeatherAPI();
        weatherModel.getWeatherDataList().observe(requireActivity(), weatherData -> {
            weatherDataList = weatherData;
        });
        Log.d("[TodayFragment] : ", weatherDataList.size() + "");

        for (int i = 0; i < weatherDataList.size(); i++) {
            Log.d("[TodayFragment] JSON : ", weatherDataList.get(i).getBaseDate() + " " +
                    weatherDataList.get(i).getBaseTime() + " " +
                    weatherDataList.get(i).getFcstDate() + " " +
                    weatherDataList.get(i).getFcstTime());
        }
    }

    public void loadWeatherAPI() {
        new Thread(() -> {
            try {
                String urlBuilder = baseURL + "?" +
                        URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey + "&" +
                        URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8") + "&" +
                        URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8") + "&" +
                        URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8") + "&" +
                        URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(paramDate, "UTF-8") + "&" +
                        URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(paramHour, "UTF-8") + "&" +
                        URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(String.valueOf((int) latitude), "UTF-8") + "&" +
                        URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(String.valueOf((int) longitude), "UTF-8");
                Log.d("[TodayFragment]", "URL: " + urlBuilder);

                URL url = new URL(urlBuilder);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                Log.d("[TodayFragment]" + tag, "Response code: " + conn.getResponseCode());

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
                Log.d("[WeatherAPI] ERROR : ", e.toString());
            }
        }).start();
    }

    public void jsonParsing(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject responseObject = (JSONObject) jsonObject.get("response");
            JSONObject bodyObject = (JSONObject) responseObject.get("body");
            JSONObject itemsObject = (JSONObject) bodyObject.get("items");
            JSONArray weatherArray = itemsObject.getJSONArray("item");

            for (int i = 0; i < weatherArray.length(); i++) { // 해당 JSONArray 객체에 값을 차례대로 가져옴
                JSONObject object = (JSONObject) weatherArray.get(i);
                String baseDate = (String) object.get("baseDate"); // 발표일자
                String baseTime = (String) object.get("baseTime"); // 발표시각
                String fcstDate = (String) object.get("fcstDate"); // 예보일자
                String fcstTime = (String) object.get("fcstTime"); // 예보시각
                String fcstValue = (String) object.get("fcstValue"); // 예보 값

                weatherModel.add(new WeatherData(baseDate, baseTime, fcstDate, fcstTime, fcstValue));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        } else {
            TodayFragment.naverMap = naverMap;
            CameraPosition cameraPosition = new CameraPosition(latLng, 15);
            UiSettings uiSettings = naverMap.getUiSettings();

            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng);
            Marker marker = new Marker();

            naverMap.setMapType(NaverMap.MapType.Terrain); // 산악 지형
            naverMap.setCameraPosition(cameraPosition);
            naverMap.setLocationSource(locationSource);
            naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            naverMap.setLayerGroupEnabled(LAYER_GROUP_MOUNTAIN, true);

            uiSettings.setLocationButtonEnabled(true);

            naverMap.moveCamera(cameraUpdate);

            naverMap.addOnCameraChangeListener((reason, animated) -> {
                Log.i("NaverMap", "카메라 변경 - reson: " + reason + ", animated: " + animated + ", camera : " + naverMap.getCameraPosition());
                marker.setPosition(naverMap.getCameraPosition().target);

                Log.i("NaverMap", naverMap.getCameraPosition().target + " " + latLng);
                if (latLng.distanceTo(naverMap.getCameraPosition().target) >= 10) {
                    marker.setMap(naverMap);
                } else {
                    marker.setMap(null);
                }
            });

            List<MountainData> mountainDataList = mountainModel.getMountainDataList().getValue();
            Log.i("MountainSIB", mountainDataList.size() + "");
            for (int i = 0; i < (mountainDataList != null ? mountainDataList.size() : 0); i++) {
                Marker mountainMarker = new Marker();
                double mountainLatitude = Double.parseDouble(mountainDataList.get(i).getMountainLatitude());
                double mountainLongitude = Double.parseDouble(mountainDataList.get(i).getMountainLongitude());
                mountainMarker.setPosition(new LatLng(mountainLatitude, mountainLongitude));
                mountainMarker.setMap(naverMap);
            }
        }
    }
}
