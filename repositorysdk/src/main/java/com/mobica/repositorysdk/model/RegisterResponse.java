package com.mobica.repositorysdk.model;

import java.util.List;

/**
 * Created by woos on 2015-11-17.
 */
public class RegisterResponse extends WsResponse {
    private String token;

    public RegisterResponse() {
        super(Status.success, null);
    }

    public RegisterResponse(Status status, String error, String token) {
        super(status, error);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "GeoFencesResponse{" +
                "status=" + getStatus() +
                ", error='" + getError() + '\'' +
                ", token=" + token +
                '}';
    }
}
