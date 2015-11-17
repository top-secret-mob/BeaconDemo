package com.mobica.discoverysdk;

import com.mobica.discoverysdk.dagger.Graphs;

import dagger.ObjectGraph;

/**
 * Entry point for discovery sdk
 */
public class DiscoverySdk {

    public static void init(ObjectGraph graph) {
        Graphs.init(graph);
    }
}
