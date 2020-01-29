package com.example.bs;

public class BusDto {
    String station;
    int nr;
    int eta;

    public BusDto(String station, int nr, int eta) {
        this.station = station;
        this.nr = nr;
        this.eta = eta;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }
}
