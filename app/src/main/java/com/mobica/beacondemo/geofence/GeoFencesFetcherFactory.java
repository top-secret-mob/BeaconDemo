package com.mobica.beacondemo.geofence;

import javax.inject.Inject;

/**
 * {@link GeoFencesFetcher} objects factory
 */
public class GeoFencesFetcherFactory {

    @Inject
    public GeoFencesFetcherFactory() {
    }

    /**
     * Creates new instance of {@link GeoFencesFetcher}
     */
    public GeoFencesFetcher create(GeoFencesFetcher.GeoFencesFetcherListener listener) {
        return new GeoFencesFetcher(listener);
    }
}
