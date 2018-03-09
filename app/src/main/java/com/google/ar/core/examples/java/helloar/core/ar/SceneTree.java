package com.google.ar.core.examples.java.helloar.core.ar;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class SceneTree<T> {
    static class ParentNotFoundException extends RuntimeException {}
    static class AlreadyAttachedException extends RuntimeException {}
    static class NotFoundException extends RuntimeException {}

    private static class ParentedSet<T> {
        Set<T> children;
        T parent;

        private ParentedSet(T parent) {
            this.parent = parent;
            this.children = new HashSet<>();
        }

        private ParentedSet(Set<T> children, T parent) {
            this.children = children;
            this.parent = parent;
        }
    }

    private final Map<Integer, ParentedSet<Integer>> tree;
    private final BiMap<T, Integer> registry;
    private AtomicInteger cnt;

    SceneTree() {
        tree = new HashMap<>();
        registry = HashBiMap.create();
        cnt = new AtomicInteger();
    }

    public Integer getID(T obj) {
        return registry.get(obj);
    }

    public T get(int id) {
        return registry.inverse().get(id);
    }

    public int add(T obj) {
        return _add(obj);
    }

    public int add(T child, T parent) {
        if (!registry.containsKey(parent)) {
            throw new ParentNotFoundException();
        }
        Integer parentID = registry.get(parent);

        if (registry.containsKey(child)) {
            throw new AlreadyAttachedException();
        }

        int childID = _add(child);
        tree.get(parentID).children.add(childID);
        return childID;
    }

    public void remove(T obj) {
        if (!registry.containsKey(obj)) {
            return;
        }

        Set<Integer> roots = new HashSet<>(1);
        Set<Integer> newRoots = new HashSet<>();
        roots.add(registry.get(obj));

        while (roots.size() > 0) {
            newRoots.clear();
            for (int root : roots) {
                newRoots.addAll(tree.remove(root).children);
                registry.remove(obj);
            }
            roots.addAll(newRoots);
        }
    }

    public Collection<T> all() {
        return registry.keySet();
    }

    public void clear() {
        tree.clear();
    }

    public void replace(T oldObj, T newObj) {
        if (!registry.containsKey(oldObj)) {
            throw new NotFoundException();
        }
        int id = registry.get(oldObj);
        registry.inverse().put(id, newObj);
    }

    boolean _contains(T obj) {
        return registry.containsKey(obj);
    }

    boolean _containsID(int id) {
        return registry.containsValue(id);
    }

    Collection<T> _subTreeElements(T root, boolean includeRoot) {
        if (!_contains(root)) {
            return new HashSet<>();
        }
        int rootID = registry.get(root);

        Set<Integer> resultIDs = new HashSet<>();
        if (includeRoot) {
            resultIDs.add(rootID);
        }

        Set<Integer> level = new HashSet<>();
        level.add(rootID);
        Set<Integer> newLevel = new HashSet<>();

        boolean called = false;
        while (newLevel.size() != 0 || !called) {
            called = true;
            newLevel.clear();
            for (Integer id : level) {
                resultIDs.addAll(tree.get(id).children);
                newLevel.addAll(tree.get(id).children);
            }
            level.clear();
            level.addAll(newLevel);
        }

        Collection<T> result = new ArrayList<>(resultIDs.size());
        for (int id : resultIDs) {
            result.add(registry.inverse().get(id));
        }
        return result;
    }

    private int _add(T obj) {
        if (!registry.containsKey(obj)) {
            int id = cnt.getAndIncrement();
            registry.put(obj, id);
            tree.put(id, new ParentedSet<>(id));
        }
        return registry.get(obj);
    }
}
