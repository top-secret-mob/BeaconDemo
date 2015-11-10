package com.mobica.beacondemo.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class MacUnregisterRequest {
    private String address;

    public MacUnregisterRequest() {
    }

    public MacUnregisterRequest(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
