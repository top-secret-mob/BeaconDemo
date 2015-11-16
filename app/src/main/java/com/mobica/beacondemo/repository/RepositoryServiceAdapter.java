package com.mobica.beacondemo.repository;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by woos on 2015-11-16.
 */
@Singleton
public class RepositoryServiceAdapter implements IRepositoryService {
    private final SettableFuture<IRepositoryService> connectionFuture = SettableFuture.create();

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

    @Override
    public ListenableFuture<Void> login() {
        return Futures.transformAsync(connectionFuture, new AsyncFunction<IRepositoryService, Void>() {
            @Override
            public ListenableFuture<Void> apply(IRepositoryService input) throws Exception {
                return input.login();
            }
        });
    }

    @Override
    public ListenableFuture<Void> logout() {
        return Futures.transformAsync(connectionFuture, new AsyncFunction<IRepositoryService, Void>() {
            @Override
            public ListenableFuture<Void> apply(IRepositoryService input) throws Exception {
                return input.logout();
            }
        });
    }
}
