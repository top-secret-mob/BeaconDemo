package com.mobica.repositorysdk.model;

/**
 * Created by woos on 2015-11-17.
 */
public class GeoFencesRequest extends WsRequest {
    private double latitude;
    private double longitude;

    public GeoFencesRequest() {
    }

    public GeoFencesRequest(String address, double latitude, double longitude) {
        super(address);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "GeoFencesRequest{" +
                "address='" + getAddress() + '\'' +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
