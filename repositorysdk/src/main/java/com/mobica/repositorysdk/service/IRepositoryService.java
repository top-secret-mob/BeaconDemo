package com.mobica.repositorysdk.service;

import android.location.Location;

import com.google.common.util.concurrent.ListenableFuture;
import com.mobica.repositorysdk.model.GeoFence;

import java.util.List;

/**
 * Created by woos on 2015-11-16.
 */
public interface IRepositoryService {

    ListenableFuture<Void> login();

    ListenableFuture<Void> logout();

    ListenableFuture<Void> registerForDiscoveryEvents();

    ListenableFuture<Void> unregisterFromDiscoveryEvents();

    ListenableFuture<List<GeoFence>> getGeoFences(Location location);
}
