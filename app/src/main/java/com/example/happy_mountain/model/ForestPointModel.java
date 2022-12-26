package com.example.happy_mountain.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happy_mountain.item.ForestPointItem;

import java.util.ArrayList;
import java.util.List;

public class ForestPointModel extends ViewModel {
    private MutableLiveData<List<ForestPointItem>> forestPointList;
    private final List<ForestPointItem> forestPointItems = new ArrayList<>();

    public void add(ForestPointItem forestPointItem) {
        forestPointItems.add(forestPointItem);
        forestPointList.postValue(forestPointItems);
    }

    public MutableLiveData<List<ForestPointItem>> getForestPointList() {
        if (forestPointList == null) {
            forestPointList = new MutableLiveData<>();
        }
        return forestPointList;
    }
}