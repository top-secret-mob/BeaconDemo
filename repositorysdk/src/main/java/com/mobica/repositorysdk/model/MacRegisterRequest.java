package com.mobica.repositorysdk.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class MacRegisterRequest extends WsRequest {
    private String gcmRegId;

    public MacRegisterRequest() {
    }

    public MacRegisterRequest(String address, String gcmRegId) {
        super(address);
        this.gcmRegId = gcmRegId;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    @Override
    public String toString() {
        return "MacRegisterRequest{" +
                "address='" + getAddress() + '\'' +
                "gcmRegId='" + gcmRegId + '\'' +
                '}';
    }
}
