package com.mobica.repositorysdk.model;

import java.util.List;

/**
 * Created by woos on 2015-11-17.
 */
public class GeoFencesResponse extends WsResponse {
    private List<GeoFence> geofences;

    public GeoFencesResponse() {
    }

    public GeoFencesResponse(Status status, String error) {
        super(status, error);
        this.geofences = geofences;
    }

    public GeoFencesResponse(List<GeoFence> geofences) {
        super(Status.success, null);
        this.geofences = geofences;
    }

    public List<GeoFence> getGeofences() {
        return geofences;
    }

    public void setGeofences(List<GeoFence> geofences) {
        this.geofences = geofences;
    }

    @Override
    public String toString() {
        return "GeoFencesResponse{" +
                "status=" + getStatus() +
                ", error='" + getError() + '\'' +
                ", geofences=" + geofences +
                '}';
    }
}
