package com.mobica.repositorysdk.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class WsRequest {
    private String address;

    public WsRequest() {
    }

    public WsRequest(String macAddress) {
        this.address = macAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "WsRequest{" +
                "address='" + address + '\'' +
                '}';
    }
}
