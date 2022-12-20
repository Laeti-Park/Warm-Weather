package com.example.happy_mountain.Model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happy_mountain.Data.LocationData;

public class LocationModel extends ViewModel {
    // Expose screen UI state
    private MutableLiveData<LocationData> locationData;

    public MutableLiveData<LocationData> getLocationData() {
        if (locationData == null) {
            locationData = new MutableLiveData<>();
            loadLocationData();
        }
        return locationData;
    }

    // Handle business logic
    private void loadLocationData() {
        // Do an asynchronous operation to fetch locationItems.
    }
}
