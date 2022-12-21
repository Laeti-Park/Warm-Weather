package com.example.happy_mountain.Data;

public class MountainData {
    private String area;
    private String mountainName;
    private String mountainNum;
    private String mountainLatitude;
    private String mountainLongitude;
    private String mountainAltitude;

    public MountainData(String area, String mountainName, String mountainNum, String mountainLatitude, String mountainLongitude, String mountainAltitude) {
        this.area = area;
        this.mountainName = mountainName;
        this.mountainNum = mountainNum;
        this.mountainLatitude = mountainLatitude;
        this.mountainLongitude = mountainLongitude;
        this.mountainAltitude = mountainAltitude;
    }

    public String getArea() {
        return area;
    }

    public String getMountainName() {
        return mountainName;
    }

    public String getMountainNum() {
        return mountainNum;
    }

    public String getMountainLatitude() {
        return mountainLatitude;
    }

    public String getMountainLongitude() {
        return mountainLongitude;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setMountainName(String mountainName) {
        this.mountainName = mountainName;
    }

    public void setMountainNum(String mountainNum) {
        this.mountainNum = mountainNum;
    }

    public void setMountainLatitude(String mountainLatitude) {
        this.mountainLatitude = mountainLatitude;
    }

    public void setMountainLongitude(String mountainLongitude) {
        this.mountainLongitude = mountainLongitude;
    }
}
