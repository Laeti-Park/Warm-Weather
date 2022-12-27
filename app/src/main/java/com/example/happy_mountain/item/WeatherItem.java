package com.example.happy_mountain.item;

public class WeatherItem {
    private final String sky; // 하늘상태
    private final String pty; // 강수형태
    private final String tmp; // 1시간 기온
    private final String reh; // 습도
    private final String vec; // 풍향
    private final String wsd; // 풍속


    public WeatherItem(String sky, String pty, String tmp, String reh, String vec, String wsd) {
        this.sky = sky;
        this.pty = pty;
        this.tmp = tmp;
        this.reh = reh;
        this.vec = vec;
        this.wsd = wsd;
    }

    public String getSky() {
        return sky;
    }

    public String getPty() {
        return pty;
    }

    public String getTmp() {
        return tmp;
    }

    public String getReh() {
        return reh;
    }

    public String getVec() {
        return vec;
    }

    public String getWsd() {
        return wsd;
    }
}
