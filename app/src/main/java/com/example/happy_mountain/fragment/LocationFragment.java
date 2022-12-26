package com.example.happy_mountain.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.happy_mountain.BuildConfig;
import com.example.happy_mountain.item.MountainItem;
import com.example.happy_mountain.item.WeatherItem;
import com.example.happy_mountain.model.LocationModel;
import com.example.happy_mountain.model.WeatherModel;
import com.example.happy_mountain.databinding.FragmentLocationBinding;
import com.example.happy_mountain.retrofit.LocationAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.MapViewEventListener;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView.POIItemEventListener;
import net.daum.mf.map.api.MapView.OpenAPIKeyAuthenticationResultListener;
import net.daum.mf.map.api.MapView.CurrentLocationEventListener;

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
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationFragment extends Fragment
        implements MapViewEventListener, POIItemEventListener, OpenAPIKeyAuthenticationResultListener, CurrentLocationEventListener {
    private final String weatherBaseURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private final String weatherKey = BuildConfig.WEATHER_API_KEY;

    private final DateFormat nowDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
    private final DateFormat nowHourFormat = new SimpleDateFormat("HH", Locale.KOREAN);
    private String paramDate;
    private String paramHour;

    private LocationModel locationModel;
    private double longitude;
    private double latitude;

    private WeatherModel weatherModel;
    private final List<WeatherItem> weatherItemList = new ArrayList<>();


    private TextView temperatureView;
    private TextView humidityView;
    private MapView mapView;
    private ImageButton myLocationButton;

    @SuppressLint("DefaultLocale")
    public LocationFragment() {
        TimeZone timeZone;
        timeZone = TimeZone.getTimeZone("Asia/Seoul");
        nowDateFormat.setTimeZone(timeZone);
        nowHourFormat.setTimeZone(timeZone);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationModel = new ViewModelProvider(requireActivity()).get(LocationModel.class);
        weatherModel = new ViewModelProvider(requireActivity()).get(WeatherModel.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLocationBinding binding = FragmentLocationBinding.inflate(inflater, container, false);
        temperatureView = binding.temperatureView;
        humidityView = binding.humidityView;
        myLocationButton = binding.myLocationButton;

        mapView = new MapView(requireContext());
        binding.mapView.addView(mapView);

        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);

        locationModel.getLocationData().observe(this.getViewLifecycleOwner(), locationItem -> {
            long now = System.currentTimeMillis();
            Date date = new Date(now);

            paramDate = updateDateFormat(nowHourFormat.format(date), date);
            paramHour = updateHourFormat(nowHourFormat.format(date));

            latitude = locationItem.getLatitude();
            longitude = locationItem.getLongitude();

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(locationItem.getLatitude(), locationItem.getLongitude()), 5, true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

            myLocationButton.setOnClickListener(v -> {
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(locationItem.getLatitude(), locationItem.getLongitude())));
            });

            updateWeather();
            updateMountainLocation(longitude, latitude);
        });


        return binding.getRoot();
    }

    private void updateMountainLocation(double longitude, double latitude) {
        String kakaoBaseURL = "https://dapi.kakao.com/";

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(kakaoBaseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        LocationAPI kakaoLocationAPI = retrofit.create(LocationAPI.class);
        String kakaoKey = "KakaoAK " + BuildConfig.KAKAO_API_KEY;
        Call<MountainItem> call =
                kakaoLocationAPI.getSearchKeyword(kakaoKey, "산", "AT4",
                        String.valueOf(longitude),
                        String.valueOf(latitude),
                        5000,
                        "distance"
                );

        call.enqueue(new Callback<MountainItem>() {

            @Override
            public void onResponse(@NonNull Call<MountainItem> call, @NonNull Response<MountainItem> response) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d("Location", "Raw: " + response.raw());
                if (Objects.requireNonNull(response.body()).getDocuments().size() > 0) {
                    Log.d("Location", "Body: " + response.body().getPlaceName(0));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MountainItem> call, @NonNull Throwable t) {
                Log.w("Location", "통신 실패 : " + t.getMessage());
            }
        });
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

    public void updateWeather() {
        loadWeatherAPI();
        weatherModel.getWeatherDataList().observe(this.getViewLifecycleOwner(), weatherItem -> {
            String currentTemperature = "현재 온도 : " + weatherItem.getTmp();
            String currentHumidity = "현재 습도 : " + weatherItem.getReh();
            temperatureView.setText(currentTemperature);
            humidityView.setText(currentHumidity);
        });
        Log.i("[Today] : ", weatherItemList.size() + "");

    }

    public void loadWeatherAPI() {
        new Thread(() -> {
            try {
                String urlBuilder = weatherBaseURL + "?" +
                        URLEncoder.encode("serviceKey", "UTF-8") + "=" + weatherKey + "&" +
                        URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8") + "&" +
                        URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8") + "&" +
                        URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8") + "&" +
                        URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(paramDate, "UTF-8") + "&" +
                        URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(paramHour, "UTF-8") + "&" +
                        URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(String.valueOf((int) latitude), "UTF-8") + "&" +
                        URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(String.valueOf((int) longitude), "UTF-8");
                Log.i("[Today]", "URL: " + urlBuilder);

                URL url = new URL(urlBuilder);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                Log.i("[Today]", "Response code: " + conn.getResponseCode());

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
                Log.i("[Today] ERROR : ", e.toString());
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

            if (weatherArray.length() > 0) {
                String sky = null;
                String pty = null;
                String tmp = null;
                String tmn = null;
                String tmx = null;
                String reh = null;
                String vec = null;
                String wsd = null;

                for (int i = 0; i < weatherArray.length(); i++) { // 해당 JSONArray 객체에 값을 차례대로 가져옴
                    JSONObject object = (JSONObject) weatherArray.get(i);
                    if (object.get("baseTime").equals(paramHour)) {
                        String category = (String) object.get("category"); // 예보 값
                        String fcstValue = (String) object.get("fcstValue"); // 예보 값

                        switch (category) {
                            case "SKY":
                                sky = fcstValue;
                                break;
                            case "PTY":
                                pty = fcstValue;
                                break;
                            case "TMP":
                                tmp = fcstValue;
                                break;
                            case "TMN":
                                tmn = fcstValue;
                                break;
                            case "TMX":
                                tmx = fcstValue;
                                break;
                            case "REH":
                                reh = fcstValue;
                                break;
                            case "VEC":
                                vec = fcstValue;
                                break;
                            case "wsd":
                                wsd = fcstValue;
                                break;
                        }
                    }
                }
                if (sky != null) {
                    weatherModel.getWeatherDataList().postValue(new WeatherItem(sky, pty, tmp, tmn, tmx, reh, vec, wsd));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(net.daum.mf.map.api.MapView mapView, int i, String s) {

    }

    @Override
    public void onPOIItemSelected(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
        updateMountainLocation(mapPoint.getMapPointGeoCoord().longitude, mapPoint.getMapPointGeoCoord().latitude);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        locationModel.getLocationData().observe(this.getViewLifecycleOwner(), locationItem -> {
            long now = System.currentTimeMillis();
            Date date = new Date(now);

            paramDate = updateDateFormat(nowHourFormat.format(date), date);
            paramHour = updateHourFormat(nowHourFormat.format(date));

            latitude = locationItem.getLatitude();
            longitude = locationItem.getLongitude();

            updateWeather();
        });
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        updateMountainLocation(mapPoint.getMapPointGeoCoord().longitude, mapPoint.getMapPointGeoCoord().latitude);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
