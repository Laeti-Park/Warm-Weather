package com.example.happy_mountain.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.happy_mountain.Model.LocationModel;
import com.example.happy_mountain.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DiseaseFragment extends Fragment {
    private final String tag = this.getClass().toString();

    private LocationModel locationModel;
    private double longitude;
    private double latitude;
    private double altitude;


    @SuppressLint("DefaultLocale")
    public DiseaseFragment() {
        TimeZone timeZone;
        DateFormat nowDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
        DateFormat nowHourFormat = new SimpleDateFormat("HH", Locale.KOREAN);
        timeZone = TimeZone.getTimeZone("Asia/Seoul");
        nowDateFormat.setTimeZone(timeZone);
        nowHourFormat.setTimeZone(timeZone);

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        String paramDate = nowDateFormat.format(date);
        String paramHour = nowHourFormat.format(date);
        if (Integer.parseInt(paramHour) % 3 != 2) {
            paramHour = String.format("%02d", Integer.parseInt(paramHour) - (1 + Integer.parseInt(paramHour) % 3)) + "00";
        } else {
            paramHour = paramHour + "00";
        }

        Log.d("[Warm-Weather]" + tag, paramDate + " " + paramHour);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationModel = new ViewModelProvider(requireActivity()).get(LocationModel.class);
        locationModel.getLocationData().observe(requireActivity(), locationItem -> {
            Log.d("hello", "hello");
            altitude = locationItem.getAltitude();
            latitude = locationItem.getLatitude();
            longitude = locationItem.getLongitude();
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_disease, container, false);
    }
}