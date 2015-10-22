package com.mobica.beacondemo.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class MacRegisterRequest {
    private String address;
    private String gcmRegId;

    public MacRegisterRequest() {
    }

    public MacRegisterRequest(String address, String gcmRegId) {
        this.address = address;
        this.gcmRegId = gcmRegId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }
}
