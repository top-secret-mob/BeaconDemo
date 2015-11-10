package com.mobica.beacondemo.geofence;

import android.os.AsyncTask;

import com.google.android.gms.location.Geofence;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Fetches list of geo fences to be used in user localization
 */
public class GeoFencesFetcher extends AsyncTask<Void, Void, GeoFencesFetcher.Result> {
    private final GeoFencesFetcherListener listener;

    /**
     * Fetching result listener
     */
    public interface GeoFencesFetcherListener {
        void onGeoFencesFetched(List<Geofence> geofences);

        void onGeoFencesFetchingFailed(Throwable error);
    }

    public class Result {
        List<Geofence> fences;
        Throwable error;

        public Result(List<Geofence> fences) {
            this.fences = fences;
        }

        public Result(Throwable error) {
            this.error = error;
        }
    }

    public GeoFencesFetcher(GeoFencesFetcherListener listener) {
        Preconditions.checkNotNull(listener, "listener must not be null");
        this.listener = listener;
    }

    @Override
    protected GeoFencesFetcher.Result doInBackground(Void... voids) {
        final Geofence kfcFence = new Geofence.Builder()
                .setRequestId("123")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setCircularRegion(53.429216, 14.555992, 100)
                .setCircularRegion(53.431702, 14.555048, 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        return new Result(Lists.newArrayList(kfcFence));
    }

    @Override
    protected void onPostExecute(GeoFencesFetcher.Result result) {
        if (result.fences != null) {
            listener.onGeoFencesFetched(result.fences);
        } else {
            listener.onGeoFencesFetchingFailed(result.error);
        }
    }
}
