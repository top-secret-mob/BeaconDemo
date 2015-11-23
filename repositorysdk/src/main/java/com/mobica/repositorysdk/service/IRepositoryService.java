package com.mobica.repositorysdk.service;

import android.location.Location;

import com.google.common.util.concurrent.ListenableFuture;
import com.mobica.repositorysdk.model.GeoFence;
import com.mobica.repositorysdk.model.WsResponse;

import java.util.List;

/**
 * Created by woos on 2015-11-16.
 */
public interface IRepositoryService {

    ListenableFuture<Void> login();

    ListenableFuture<Void> forceLogin();

    ListenableFuture<WsResponse> logout();

    ListenableFuture<WsResponse> subscribeForDiscoveryEvents();

    ListenableFuture<WsResponse> unsubscribeFomDiscoveryEvents();

    ListenableFuture<List<GeoFence>> getGeoFences(Location location);
}
