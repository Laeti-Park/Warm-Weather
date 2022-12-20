package com.example.happy_mountain.Data;

public class WeatherData {
    private String baseDate; // 발표일자
    private String baseTime; // 발표시각
    private String fcstDate; // 예보일자
    private String fcstTime; // 예보시각
    private String fcstValue; // 예보 값

    public WeatherData(String baseDate, String baseTime, String fcstDate, String fcstTime, String fcstValue) {
        this.baseDate = baseDate;
        this.baseTime = baseTime;
        this.fcstDate = fcstDate;
        this.fcstTime = fcstTime;
        this.fcstValue = fcstValue;
    }

    public String getBaseDate() {
        return baseDate;
    }

    public String getBaseTime() {
        return baseTime;
    }

    public String getFcstDate() {
        return fcstDate;
    }

    public String getFcstTime() {
        return fcstTime;
    }

    public String getFcstValue() {
        return fcstValue;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }

    public void setBaseTime(String baseTime) {
        this.baseTime = baseTime;
    }

    public void setFcstDate(String fcstDate) {
        this.fcstDate = fcstDate;
    }

    public void setFcstTime(String fcstTime) {
        this.fcstTime = fcstTime;
    }

    public void setFcstValue(String fcstValue) {
        this.fcstValue = fcstValue;
    }
}
