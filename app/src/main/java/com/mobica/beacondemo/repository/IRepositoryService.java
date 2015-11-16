package com.mobica.beacondemo.repository;

import android.os.IBinder;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by woos on 2015-11-16.
 */
public interface IRepositoryService {

    ListenableFuture<Void> login();

    ListenableFuture<Void> logout();
}
