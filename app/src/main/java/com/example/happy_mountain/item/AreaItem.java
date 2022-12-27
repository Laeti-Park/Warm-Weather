package com.example.happy_mountain.item;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AreaItem {
    @SerializedName("documents")
    @Expose
    private final List<Place> documents;

    public AreaItem(List<Place> documents) {
        this.documents = documents;
    }

    public List<Place> getDocuments() {
        return this.documents;
    }

    public String getAddressName() {
        return this.documents.get(0).addressName;
    }

    public Double getX() {
        return Double.parseDouble(this.documents.get(0).x);
    }

    public Double getY() {
        return Double.parseDouble(this.documents.get(0).y);
    }

    static class Place {
        @SerializedName("address_name")
        @Expose
        private String addressName;
        @SerializedName("x")
        @Expose
        private String x;
        @SerializedName("y")
        @Expose
        private String y;
    }
}
