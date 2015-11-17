package com.mobica.repositorysdk.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class MacUnregisterRequest extends WsRequest {
    private String address;

    public MacUnregisterRequest() {
    }

    public MacUnregisterRequest(String address) {
        super(address);
    }

    @Override
    public String toString() {
        return "MacRegisterRequest{" +
                "address='" + getAddress() + '\'' +
                '}';
    }
}
