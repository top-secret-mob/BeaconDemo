package com.mobica.repositorysdk.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class DiscoveryUnregisterRequest extends WsRequest {
    public DiscoveryUnregisterRequest() {
    }

    public DiscoveryUnregisterRequest(String address) {
        super(address);
    }

    @Override
    public String toString() {
        return "MacRegisterRequest{" +
                "address='" + getAddress() + '\'' +
                '}';
    }
}
