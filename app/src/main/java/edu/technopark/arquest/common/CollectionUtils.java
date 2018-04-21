package edu.technopark.arquest.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {
    public static <T> List<T> singleItemList(T item) {
        List<T> result = new ArrayList<>(1);
        result.add(item);
        return result;
    }

    public static <T> T first(Collection<T> ts) {
        return ts.iterator().next();
    }

    @SafeVarargs
    public static <T> List<T> listOf(T ...items) {
        List<T> result = new ArrayList<>(items.length);
        for (int i = 0; i != items.length; ++i) {
            result.add(items[i]);
        }
        return result;
    }
}
