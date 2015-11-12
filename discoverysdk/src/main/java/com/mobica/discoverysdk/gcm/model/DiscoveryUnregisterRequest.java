package com.mobica.discoverysdk.gcm.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class DiscoveryUnregisterRequest {
    private String address;

    public DiscoveryUnregisterRequest() {
    }

    public DiscoveryUnregisterRequest(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
