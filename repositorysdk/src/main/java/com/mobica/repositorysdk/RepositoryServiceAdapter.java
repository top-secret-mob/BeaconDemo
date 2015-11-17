package com.mobica.repositorysdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.mobica.repositorysdk.model.GeoFence;
import com.mobica.repositorysdk.model.GeoFencesResponse;
import com.mobica.repositorysdk.service.IRepositoryService;
import com.mobica.repositorysdk.service.RepositoryService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by woos on 2015-11-16.
 */
@Singleton
public class RepositoryServiceAdapter implements IRepositoryService {
    private final SettableFuture<IRepositoryService> connectionFuture = SettableFuture.create();
    private ListenableFuture<Void> loginFuture;

    @Inject
    public RepositoryServiceAdapter() {
    }

    public void connect(Context context) {
        context.bindService(new Intent(context, RepositoryService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            connectionFuture.set(((RepositoryService.LocalBinder) iBinder).getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            connectionFuture.setException(new RuntimeException("Service disconnected"));
        }
    };

    private synchronized ListenableFuture<Void> loginFuture() {
        if (loginFuture == null) {
            throw new IllegalStateException("Must be logged in first");
        }
        return loginFuture;
    }

    private ListenableFuture<IRepositoryService> afterInit() {
        return Futures.transformAsync(connectionFuture, new AsyncFunction<IRepositoryService, IRepositoryService>() {
            @Override
            public ListenableFuture<IRepositoryService> apply(final IRepositoryService service) throws Exception {
                return Futures.transformAsync(loginFuture(), new AsyncFunction<Void, IRepositoryService>() {
                    @Override
                    public ListenableFuture<IRepositoryService> apply(Void input) throws Exception {
                        return Futures.immediateFuture(service);
                    }
                });
            }
        });
    }

    @Override
    public synchronized ListenableFuture<Void> login() {
        loginFuture = Futures.transformAsync(connectionFuture, new AsyncFunction<IRepositoryService, Void>() {
            @Override
            public ListenableFuture<Void> apply(IRepositoryService service) throws Exception {
                return service.login();
            }
        });
        return loginFuture;
    }

    @Override
    public synchronized ListenableFuture<Void> logout() {
        loginFuture = null;
        return Futures.transformAsync(afterInit(), new AsyncFunction<IRepositoryService, Void>() {
            @Override
            public ListenableFuture<Void> apply(IRepositoryService service) throws Exception {
                return service.logout();
            }
        });
    }

    @Override
    public ListenableFuture<Void> registerForDiscoveryEvents() {
        return Futures.transformAsync(afterInit(), new AsyncFunction<IRepositoryService, Void>() {
            @Override
            public ListenableFuture<Void> apply(IRepositoryService service) throws Exception {
                return service.registerForDiscoveryEvents();
            }
        });
    }

    @Override
    public ListenableFuture<Void> unregisterFromDiscoveryEvents() {
        return Futures.transformAsync(afterInit(), new AsyncFunction<IRepositoryService, Void>() {
            @Override
            public ListenableFuture<Void> apply(IRepositoryService service) throws Exception {
                return service.unregisterFromDiscoveryEvents();
            }
        });
    }

    @Override
    public ListenableFuture<List<GeoFence>> getGeoFences(final Location location) {
        return Futures.transformAsync(afterInit(), new AsyncFunction<IRepositoryService, List<GeoFence>>() {
            @Override
            public ListenableFuture<List<GeoFence>> apply(IRepositoryService service) throws Exception {
                return service.getGeoFences(location);
            }
        });
    }
}
