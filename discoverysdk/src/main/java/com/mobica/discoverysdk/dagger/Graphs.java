package com.mobica.discoverysdk.dagger;

import dagger.ObjectGraph;

/**
 * Created by woos on 2015-11-10.
 */
public class Graphs {
    private static ObjectGraph graph;

    public static void init(Object module) {
        Graphs.graph = ObjectGraph.create(module);
    }

    public static <T> T inject(T injectable) {
        return graph.inject(injectable);
    }
}
