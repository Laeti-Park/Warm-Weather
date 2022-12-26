package com.example.happy_mountain.item;

public class ForestPointItem {
    private final String time;
    private final String area;
    private final String location;
    private final String humidity;
    private final String windSpeed;
    private final String warningRate;

    public ForestPointItem(String time, String area, String location, String humidity, String windSpeed, String warningRate) {
        this.time = time;
        this.area = area;
        this.location = location;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.warningRate = warningRate;
    }

    public String getArea() {
        return area;
    }

    public String getLocation() {
        return location;
    }

    public String getWarningRate() {
        return warningRate;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }
}
