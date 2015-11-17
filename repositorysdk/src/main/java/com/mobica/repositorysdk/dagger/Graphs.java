package com.mobica.repositorysdk.dagger;

import dagger.ObjectGraph;

/**
 * Created by woos on 2015-11-10.
 */
public class Graphs {
    private static ObjectGraph graph;

    public static void init(ObjectGraph graph) {
        Graphs.graph = graph;
    }

    public static <T> T inject(T injectable) {
        return graph.inject(injectable);
    }
}
