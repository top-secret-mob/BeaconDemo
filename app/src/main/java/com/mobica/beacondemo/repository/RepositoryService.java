package com.mobica.beacondemo.repository;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mobica.beacondemo.BeaconApplication;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class RepositoryService extends Service implements IRepositoryService {
    private final ListeningExecutorService threadPool =
            MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    private final LocalBinder binder = new LocalBinder();
    @Inject
    RegistrationProvider registrationProvider;

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
        BeaconApplication.getGraph().inject(this);
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
}
