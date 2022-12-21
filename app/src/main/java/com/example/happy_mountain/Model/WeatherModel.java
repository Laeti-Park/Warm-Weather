package com.example.happy_mountain.Model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happy_mountain.Data.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class WeatherModel extends ViewModel {
    private MutableLiveData<List<WeatherData>> weatherDataList;
    List<WeatherData> items = new ArrayList<>();

    public void add(WeatherData weatherData) {
        items.add(weatherData);
        weatherDataList.postValue(items);
    }

    public MutableLiveData<List<WeatherData>> getWeatherDataList() {
        if (weatherDataList == null) {
            weatherDataList = new MutableLiveData<>();
            loadWeatherData();
        }
        return weatherDataList;
    }

    // Handle business logic
    private void loadWeatherData() {
        // Do an asynchronous operation to fetch weatherItems.
    }
}
