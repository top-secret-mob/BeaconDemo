package com.mobica.repositorysdk.dagger;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.mobica.repositorysdk.ICredentials;
import com.mobica.repositorysdk.RepositoryAdapter;
import com.mobica.repositorysdk.service.GeofenceProvider;
import com.mobica.repositorysdk.service.RegistrationProvider;
import com.mobica.repositorysdk.volley.CertsManager;

import dagger.Module;

/**
 * Created by woos on 2015-11-17.
 */
@Module(injects = {RepositoryAdapter.class, RegistrationProvider.class, GeofenceProvider.class,
        RequestQueue.class, Context.class, ICredentials.class, CertsManager.class}, library = true, complete = false)
public class RepositoryModule {
}
