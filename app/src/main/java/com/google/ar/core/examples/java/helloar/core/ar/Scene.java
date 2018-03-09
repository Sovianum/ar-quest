package com.google.ar.core.examples.java.helloar.core.ar;

import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.examples.java.helloar.core.ar.record.ObjectRecord;
import com.google.ar.core.examples.java.helloar.core.ar.record.SceneRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Scene extends SceneTree<Pose> {
    private Map<Integer, Anchor> anchorMap;

    public Scene() {
        super();
        anchorMap = new HashMap<>();
    }

    // it is assumed that parent id is always less than child id
    public Map<String, Collection<ObjectRecord>> load(final SceneRecord sceneRecord) {
        return load(sceneRecord, Pose.IDENTITY);
    }

    // it is assumed that parent id is always less than child id
    public Map<String, Collection<ObjectRecord>> load(final SceneRecord sceneRecord, Pose origin) {
        Map<String, Collection<ObjectRecord>> result = new HashMap<>();

        for (ObjectRecord objectRecord : sceneRecord.getObjectRecords()) {
            if (!result.containsKey(objectRecord.getName())) {
                result.put(objectRecord.getName(), new ArrayList<ObjectRecord>());
            }

            int sceneId;
            if (objectRecord.getParentId() > 0) {
                int parentSceneId = sceneRecord.getById(objectRecord.getParentId()).getSceneId();
                // todo check if always need to use relative offset
                sceneId = savePose(objectRecord.getPoseRecord().buildPose().compose(origin), parentSceneId, true);
            } else {
                sceneId = savePose(objectRecord.getPoseRecord().buildPose().compose(origin));
            }

            objectRecord.setSceneId(sceneId);
            result.get(objectRecord.getName()).add(objectRecord);
        }

        return result;
    }

    public Map<Integer, Anchor> anchorMap() {
        return anchorMap;
    }

    public void updateAnchors(Session session) {
        for (Map.Entry<Integer, Anchor> entry: anchorMap.entrySet()) {
            entry.getValue().detach();
        }
        anchorMap.clear();

        for (Integer id : ids()) {
            anchorMap.put(id, session.createAnchor(get(id)));
        }
    }

    public int savePose(Pose pose) {
        return add(pose);
    }

    public int savePose(Pose pose, int parentID, boolean isRelative) {
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

        return add(transform, parentPose);
    }

    public void applyGlobal(int id, Pose pose) {
        _apply(id, pose, false);
    }

    public void apply(int id, Pose pose) {
        _apply(id, pose, true);
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
