package com.mobica.repositorysdk.utils;

/**
 * Created by woos on 2015-11-16.
 */
public class FluentExecutors {
    private static final MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

    public static MainThreadExecutor mainThreadExecutor() {
        return mainThreadExecutor;
    }
}
