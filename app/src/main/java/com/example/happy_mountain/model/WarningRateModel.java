package com.example.happy_mountain.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happy_mountain.item.WarningRateItem;
import java.util.List;

public class WarningRateModel extends ViewModel {
    private MutableLiveData<List<WarningRateItem>> warningRateList;
    private MutableLiveData<String> dateInfo;

    public MutableLiveData<String> getDateInfo() {
        if (dateInfo == null) {
            dateInfo = new MutableLiveData<>();
        }
        return dateInfo;
    }

    public MutableLiveData<List<WarningRateItem>> getWarningRateList() {
        if (warningRateList == null) {
            warningRateList = new MutableLiveData<>();
        }
        return warningRateList;
    }
}