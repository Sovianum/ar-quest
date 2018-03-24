package com.google.ar.core.examples.java.helloar.common;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtils {
    public static <T> Collection<T> singleItemCollection(T item) {
        Collection<T> result = new ArrayList<>(1);
        result.add(item);
        return result;
    }

    public static <T> T first(Collection<T> ts) {
        return ts.iterator().next();
    }
}
