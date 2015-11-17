package com.mobica.repositorysdk.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mobica.repositorysdk.dagger.Graphs;
import com.mobica.repositorysdk.model.GeoFence;
import com.mobica.repositorysdk.model.GeoFencesResponse;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class RepositoryService extends Service implements IRepositoryService {
    private final ListeningExecutorService threadPool =
            MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    private final LocalBinder binder = new LocalBinder();
    @Inject
    RegistrationProvider registrationProvider;
    @Inject
    GeofenceProvider geofenceProvider;

    public class LocalBinder extends Binder {
        public IRepositoryService getService() {
            return RepositoryService.this;
        }
    }

    public RepositoryService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Graphs.inject(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public ListenableFuture<Void> login() {
        return threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                registrationProvider.login();
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<Void> logout() {
        return threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                registrationProvider.logout();
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<Void> registerForDiscoveryEvents() {
        return threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                registrationProvider.registerForDiscoveryEvents();
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<Void> unregisterFromDiscoveryEvents() {
        return threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                registrationProvider.unregisterFromDiscoveryEvents();
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<List<GeoFence>> getGeoFences(final Location location) {
        return threadPool.submit(new Callable<List<GeoFence>>() {
            @Override
            public List<GeoFence> call() throws Exception {
                final GeoFencesResponse response = geofenceProvider.getGeoFences(location);
                return response.getGeofences();
            }
        });
    }
}
