package com.example.warm_weather.API;

import android.util.Log;

import com.example.warm_weather.BuildConfig;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;


public class WeatherAPI {
    private final String tag = this.getClass().toString();
    private String baseURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private String serviceKey = BuildConfig.API_KEY;
    private String date, hour, x, y;

    /**
     * URL : http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst
     * pageNo : 1
     * numOfRows : 1000
     * dataType : JSON
     * ServiceKey
     *
     * @param date = base_date
     * @param hour = base_time
     * @param x    = nx
     * @param y    = ny
     */
    public WeatherAPI(String date, String hour, String x, String y) {
        this.date = date;
        this.hour = hour;
        this.x = x;
        this.y = y;

        init();
    }

    public void init() {
        new Thread(() -> {
            try {
                String urlBuilder = baseURL + "?" +
                        URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey + "&" +
                        URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8") + "&" +
                        URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8") + "&" +
                        URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8") + "&" +
                        URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&" +
                        URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(hour, "UTF-8") + "&" +
                        URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(x, "UTF-8") + "&" +
                        URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(y, "UTF-8");
                Log.d("[Warm-Weather]" + tag, "URL: " + urlBuilder);

                URL url = new URL(urlBuilder);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                Log.d("[Warm-Weather]" + tag, "Response code: " + conn.getResponseCode());

                BufferedReader rd;
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
                Log.d("[Warm-Weather]" + tag, sb.toString());
            } catch (IOException e) {
                Log.d("[Warm-Weather]" + tag, e.toString());
            }
        }).start();
    }
}