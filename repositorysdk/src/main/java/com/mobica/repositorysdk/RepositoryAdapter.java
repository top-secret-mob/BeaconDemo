package com.mobica.repositorysdk;

import android.content.Context;
import android.location.Location;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mobica.repositorysdk.model.GeoFence;
import com.mobica.repositorysdk.model.GeoFencesResponse;
import com.mobica.repositorysdk.model.RegisterResponse;
import com.mobica.repositorysdk.model.WsResponse;
import com.mobica.repositorysdk.service.GeofenceProvider;
import com.mobica.repositorysdk.service.IRepositoryService;
import com.mobica.repositorysdk.service.RegistrationProvider;
import com.mobica.repositorysdk.volley.CertsManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by woos on 2015-11-16.
 */
@Singleton
public class RepositoryAdapter implements IRepositoryService {
    private ListenableFuture<Void> loginFuture;

    private RegistrationProvider registrationProvider;
    private GeofenceProvider geofenceProvider;
    private ICredentials credentials;

    @Inject
    public RepositoryAdapter(Context context, RegistrationProvider registrationProvider,
                             GeofenceProvider geofenceProvider, ICredentials credentials, CertsManager certsManager) {
        this.registrationProvider = registrationProvider;
        this.geofenceProvider = geofenceProvider;
        this.credentials = credentials;

        certsManager.addTrustedHost(context.getString(R.string.host));
    }

    private ListenableFuture<Void> afterLogin() {
        if (loginFuture == null) {
            return Futures.immediateFailedFuture(new IllegalStateException("Must be logged in first"));
        }
        return loginFuture;
    }

    @Override
    public synchronized ListenableFuture<Void> login() {
        if (loginFuture != null) {
            return loginFuture;
        }

        return forceLogin();
    }

    @Override
    public ListenableFuture<Void> forceLogin() {
        loginFuture = Futures.transformAsync(registrationProvider.login(), new AsyncFunction<RegisterResponse, Void>() {
            @Override
            public ListenableFuture<Void> apply(RegisterResponse response) throws Exception {
                credentials.setAuthToken(response.getToken());
                return Futures.immediateFuture(null);
            }
        });
        return loginFuture;
    }

    @Override
    public synchronized ListenableFuture<WsResponse> logout() {
        loginFuture = null;
        return Futures.transformAsync(afterLogin(), new AsyncFunction<Void, WsResponse>() {
            @Override
            public ListenableFuture<WsResponse> apply(Void none) throws Exception {
                return registrationProvider.logout();
            }
        });
    }

    @Override
    public ListenableFuture<WsResponse> subscribeForDiscoveryEvents() {
        return Futures.transformAsync(afterLogin(), new AsyncFunction<Void, WsResponse>() {
            @Override
            public ListenableFuture<WsResponse> apply(Void none) throws Exception {
                return registrationProvider.subscribeForDiscoveryEvents();
            }
        });
    }

    @Override
    public ListenableFuture<WsResponse> unsubscribeFomDiscoveryEvents() {
        return Futures.transformAsync(afterLogin(), new AsyncFunction<Void, WsResponse>() {
            @Override
            public ListenableFuture<WsResponse> apply(Void none) throws Exception {
                return registrationProvider.unsubscribeFromDiscoveryEvents();
            }
        });
    }

    @Override
    public ListenableFuture<List<GeoFence>> getGeoFences(final Location location) {
        return Futures.transformAsync(afterLogin(), new AsyncFunction<Void, List<GeoFence>>() {
            @Override
            public ListenableFuture<List<GeoFence>> apply(Void none) throws Exception {
                return Futures.transform(geofenceProvider.getGeoFences(location), new Function<GeoFencesResponse,
                        List<GeoFence>>() {
                    @Override
                    public List<GeoFence> apply(GeoFencesResponse response) {
                        return response.getGeofences();
                    }
                });
            }
        });
    }
}
