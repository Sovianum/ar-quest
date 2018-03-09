package com.google.ar.core.examples.java.helloar.core.ar;

import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.record.ObjectRecord;
import com.google.ar.core.examples.java.helloar.core.ar.record.ObjectRecordStorage;
import com.google.ar.core.examples.java.helloar.core.ar.record.SceneRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Scene extends SceneTree<Pose> {
    private Map<Integer, Anchor> anchorMap;
    private Map<Integer, Collider> colliderMap;

    public Scene() {
        super();
        anchorMap = new HashMap<>();
        colliderMap = new HashMap<>();
    }

    public Map<Integer, Anchor> getAnchorMap() {
        return anchorMap;
    }

    // it is assumed that parent id is always less than child id
    public ObjectRecordStorage load(final SceneRecord sceneRecord) {
        return load(sceneRecord, Pose.IDENTITY);
    }

    // it is assumed that parent id is always less than child id
    public ObjectRecordStorage load(final SceneRecord sceneRecord, Pose origin) {
        for (ObjectRecord objectRecord : sceneRecord.getObjectRecords()) {
            int sceneId;
            if (objectRecord.getParentId() > 0) {
                int parentSceneId = sceneRecord.getById(objectRecord.getParentId()).getSceneId();
                // todo check if always need to use relative offset
                sceneId = addObject(objectRecord.getPoseRecord().buildPose().compose(origin), parentSceneId, true);
            } else {
                sceneId = addObject(objectRecord.getPoseRecord().buildPose().compose(origin));
            }

            objectRecord.setSceneId(sceneId);
        }

        return sceneRecord.getObjectStorage();
    }

    public void update(Session session) {
        updateAnchors(session);
        updateColliders();
    }

    public Collection<Integer> getCollisions(final Collider collider) {
        Collection<Integer> result = new ArrayList<>();
        if (collider == null) {
            return result;
        }

        for (Map.Entry<Integer, Collider> entry : colliderMap.entrySet()) {
            int itemID = entry.getKey();
            Collider itemCollider = entry.getValue();
            if (itemCollider == null) {
                continue;
            }
            if (collider.collide(itemCollider)) {
                result.add(itemID);
            }
        }

        return result;
    }

    public Map<Integer, Collider> getColliderMap() {
        return colliderMap;
    }

    public void applyGlobal(int id, Pose pose) {
        _apply(id, pose, false);
    }

    public void apply(int id, Pose pose) {
        _apply(id, pose, true);
    }

    public boolean setCollider(int id, Collider collider) {
        if (!colliderMap.containsKey(id)) {
            return false;
        }
        colliderMap.put(id, collider);
        return true;
    }

    public int addObject(Pose pose) {
        return addObject(pose, new Collider());
    }

    public int addObject(Pose pose, Collider collider) {
        int id = add(pose);
        colliderMap.put(id, collider);
        return id;
    }

    public int addObject(Pose pose, int parentID, boolean isRelative) {
        return addObject(
                pose, new Collider(), parentID, isRelative
        );
    }

    public int addObject(Pose pose, Collider collider, int parentID, boolean isRelative) {
        Pose parentPose = get(parentID);
        if (parentPose == null) {
            throw new ParentNotFoundException();
        }
        Pose transform;
        if (isRelative) {
            transform = pose.compose(parentPose);
        } else {
            transform = pose;
        }

        int id = add(transform, parentPose);
        colliderMap.put(id, collider);
        return id;
    }

    public void updateColliders() {
        for (Map.Entry<Integer, Collider> entry : colliderMap.entrySet()) {
            entry.getValue().setPosition(get(entry.getKey()));
        }
    }

    private void updateAnchors(Session session) {
        for (Map.Entry<Integer, Anchor> entry: anchorMap.entrySet()) {
            entry.getValue().detach();
        }
        anchorMap.clear();

        for (Integer id : ids()) {
            anchorMap.put(id, session.createAnchor(get(id)));
        }
    }

    private void _apply(int id, Pose pose, boolean local) {
        if (!_containsID(id)) {
            return;
        }

        Collection<Pose> poses = _subTreeElements(get(id), true);
        if (poses == null) {
            return;
        }

        for (Pose localPose : poses) {
            replace(localPose, local ? pose.compose(localPose) : pose);
        }
    }
}
