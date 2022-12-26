package com.example.happy_mountain.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happy_mountain.item.LocationItem;

public class LocationModel extends ViewModel {
    private MutableLiveData<LocationItem> locationData;

    public MutableLiveData<LocationItem> getLocationData() {
        if (locationData == null) {
            locationData = new MutableLiveData<>();
        }
        return locationData;
    }
}
