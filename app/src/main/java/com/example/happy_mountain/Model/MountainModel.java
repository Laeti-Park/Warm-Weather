package com.example.happy_mountain.Model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happy_mountain.Data.MountainData;

import java.util.List;

public class MountainModel extends ViewModel {
    private MutableLiveData<List<MountainData>> mountainDataList;

    public MutableLiveData<List<MountainData>> getMountainDataList() {
        if (mountainDataList == null) {
            mountainDataList = new MutableLiveData<>();
            loadMountainData();
        }
        return mountainDataList;
    }

    // Handle business logic
    private void loadMountainData() {
        // Do an asynchronous operation to fetch weatherItems.
    }
}
