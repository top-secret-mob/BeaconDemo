package com.mobica.repositorysdk.dagger;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mobica.repositorysdk.ICredentials;
import com.mobica.repositorysdk.service.GeofenceProvider;
import com.mobica.repositorysdk.service.RegistrationProvider;
import com.mobica.repositorysdk.service.RepositoryService;

import dagger.Module;
import dagger.Provides;

/**
 * Created by woos on 2015-11-17.
 */
@Module(injects = {RepositoryService.class, RegistrationProvider.class, GeofenceProvider.class, ICredentials.class,
        RequestQueue.class, Context.class}, library = true, complete = false)
public class RepositoryModule {
    private final ICredentials credentials;
    private final RequestQueue requestQueue;

    public RepositoryModule(Context context, ICredentials credentials) {
        this.credentials = credentials;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @Provides
    public ICredentials provideCredentials() {
        return credentials;
    }

    @Provides
    public RequestQueue provideRequestQueue() {
        return requestQueue;
    }
}
