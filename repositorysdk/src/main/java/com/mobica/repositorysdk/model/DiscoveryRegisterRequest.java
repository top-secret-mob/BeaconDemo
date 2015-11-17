package com.mobica.repositorysdk.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class DiscoveryRegisterRequest extends WsRequest {
    public DiscoveryRegisterRequest() {
    }

    public DiscoveryRegisterRequest(String address) {
        super(address);
    }

    @Override
    public String toString() {
        return "MacRegisterRequest{" +
                "address='" + getAddress() + '\'' +
                '}';
    }
}
