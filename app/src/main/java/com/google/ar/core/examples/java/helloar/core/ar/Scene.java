package com.google.ar.core.examples.java.helloar.core.ar;

import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene extends Tree<SceneObject> {
    private Map<Integer, Anchor> anchorMap;
    private boolean loaded = false;

    public Scene() {
        super();
        anchorMap = new HashMap<>();
    }

    public Map<Integer, Anchor> getAnchorMap() {
        return anchorMap;
    }

    // it is assumed that parent id is always less than child id
    public void load(final List<SceneObject> sceneObjects) {
        load(sceneObjects, Pose.IDENTITY);
        loaded = true;
    }

    // it is assumed that parent id is always less than child id
    public void load(final List<SceneObject> sceneObjects, Pose origin) {
        sceneObjects.sort(new Comparator<SceneObject>() {
            @Override
            public int compare(SceneObject o1, SceneObject o2) {
                return o1.getIdentifiable().getId() - o2.getIdentifiable().getId();
            }
        });

        Map<Integer, Integer> index = new HashMap<>();
        for (int i = 0; i != sceneObjects.size(); ++i) {
            index.put(sceneObjects.get(i).getIdentifiable().getId(), i);
        }

        for (SceneObject obj : sceneObjects) {
            int parentID = obj.getIdentifiable().getParentID();
            obj.getGeom().apply(origin);
            Collider collider = obj.getCollider();
            if (collider != null) {
                collider.setPosition(obj.getGeom());
            }

            if (parentID > 0) {
                int parentSceneID = sceneObjects.get(index.get(parentID)).getIdentifiable().getSceneID();
                addObject(obj, parentSceneID, false);
            } else {
                addObject(obj);
            }
        }
        loaded = true;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void reset() {
        anchorMap = new HashMap<>();
        loaded = false;
    }

    public void update(Session session) {
        for (Map.Entry<Integer, Anchor> entry: anchorMap.entrySet()) {
            entry.getValue().detach();
        }
        anchorMap.clear();

        for (Integer id : ids()) {
            SceneObject object = get(id);
            if (object == null || !object.isEnabled()) {
                continue;
            }
            anchorMap.put(id, session.createAnchor(object.getGeom().getPose()));
        }
    }

    public Collection<SceneObject> getCollisions(final Collider collider) {
        Collection<SceneObject> result = new ArrayList<>();
        getCollisions(collider, result);
        return result;
    }

    public void getCollisions(final Collider collider, final Collection<SceneObject> acceptor) {
        if (collider == null) {
            return;
        }
        acceptor.clear();

        for (Map.Entry<SceneObject, Integer> entry : getRegistry().entrySet()) {
            if (!entry.getKey().isEnabled()) {
                continue;
            }

            Collider itemCollider = entry.getKey().getCollider();
            if (itemCollider == null) {
                continue;
            }
            if (collider.collide(itemCollider)) {
                acceptor.add(entry.getKey());
            }
        }
    }

    public boolean addObject(SceneObject obj) {
        obj.getIdentifiable().setSceneID(add(obj));
        return true;
    }

    public boolean addObject(SceneObject obj, int parentID, boolean isRelative) {
        SceneObject parent = get(parentID);
        if (parent == null) {
            return false;
        }

        if (isRelative) {
            obj.getGeom().apply(parent.getGeom().getPose());
        }

        obj.getIdentifiable().setSceneID(add(obj, parent));
        return true;
    }

    public void apply(int id, Pose pose) {
        if (!containsID(id)) {
            return;
        }
        Collection<SceneObject> sceneObjects = getSubTreeElements(get(id), true);
        if (sceneObjects == null) {
            return;
        }
        for (SceneObject sceneObject : sceneObjects) {
            sceneObject.getGeom().apply(pose);
        }
    }
}
