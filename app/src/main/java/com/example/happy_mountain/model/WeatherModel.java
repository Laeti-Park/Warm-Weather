package com.example.happy_mountain.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happy_mountain.item.WeatherItem;

public class WeatherModel extends ViewModel {
    private MutableLiveData<WeatherItem> weatherDataList;

    public MutableLiveData<WeatherItem> getWeatherDataList() {
        if (weatherDataList == null) {
            weatherDataList = new MutableLiveData<>();
        }
        return weatherDataList;
    }
}
