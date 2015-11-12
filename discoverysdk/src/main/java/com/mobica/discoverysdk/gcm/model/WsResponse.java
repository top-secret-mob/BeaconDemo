package com.mobica.discoverysdk.gcm.model;

/**
 * Created by wojtek on 22.10.15.
 */
public class WsResponse {
    public enum Status {
        success,
        error
    }

    private Status status;
    private String error;

    public WsResponse() {
    }

    public WsResponse(Status status, String error) {
        this.status = status;
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "WsResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                '}';
    }
}
