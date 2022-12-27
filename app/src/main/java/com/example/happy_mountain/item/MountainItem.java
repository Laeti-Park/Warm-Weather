package com.example.happy_mountain.item;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class MountainItem {
    @SerializedName("documents")
    @Expose
    private final List<Place> documents;

    public MountainItem(List<Place> documents) {
        this.documents = documents;
    }

    public List<Place> getDocuments() {
        return this.documents;
    }

    public String getPlaceName(int num) {
        return this.documents.get(num).placeName;
    }

    public String getAddressName(int num) {
        return this.documents.get(num).addressName;
    }

    public String getRoadAddressName(int num) {
        return this.documents.get(num).roadAddressName;
    }

    public Double getX(int num) {
        return Double.parseDouble(this.documents.get(num).x);
    }

    public Double getY(int num) {
        return Double.parseDouble(this.documents.get(num).y);
    }

    static class Place {
        @SerializedName("place_name")
        @Expose
        private String placeName;
        @SerializedName("address_name")
        @Expose
        private String addressName;
        @SerializedName("road_address_name")
        @Expose
        private String roadAddressName;
        @SerializedName("x")
        @Expose
        private String x;
        @SerializedName("y")
        @Expose
        private String y;
    }
}

