package com.google.ar.core.examples.java.helloar.scene;

import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.examples.java.helloar.scene.record.ObjectRecord;
import com.google.ar.core.examples.java.helloar.scene.record.SceneRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Scene extends SceneTree<Anchor> {
    private Session session;

    public Scene(Session session) {
        super();
        this.session = session;
    }

    // it is assumed that parent id is always less than child id
    public Map<String, Collection<ObjectRecord>> load(final SceneRecord sceneRecord) {
        Map<String, Collection<ObjectRecord>> result = new HashMap<>();

        for (ObjectRecord objectRecord : sceneRecord.getObjectRecords()) {
            if (!result.containsKey(objectRecord.getName())) {
                result.put(objectRecord.getName(), new ArrayList<ObjectRecord>());
            }

            int sceneId;
            if (objectRecord.getParentId() > 0) {
                int parentSceneId = sceneRecord.getById(objectRecord.getParentId()).getSceneId();
                // todo check if always need to use relative offset
                sceneId = createAnchor(objectRecord.getPoseRecord().buildPose(), parentSceneId, true);
            } else {
                sceneId = createAnchor(objectRecord.getPoseRecord().buildPose());
            }

            objectRecord.setSceneId(sceneId);
            result.get(objectRecord.getName()).add(objectRecord);
        }

        return result;
    }

    public void clear() {
        for (Anchor a : super.all()) {
            a.detach();
        }
        super.clear();
    }

    public void reAttachAnchors(Session session) {
        this.session = session;
        for (Anchor anchor : all()) {
            if (anchor.getTrackingState() != TrackingState.TRACKING) {
                replace(anchor, session.createAnchor(anchor.getPose()));
            }
        }
    }

    public int createAnchor(Pose pose) {
        Anchor anchor = session.createAnchor(pose);
        return add(anchor);
    }

    public int createAnchor(Pose pose, int parentID, boolean isRelative) {
        Anchor parent = get(parentID);
        if (parent == null) {
            throw new ParentNotFoundException();
        }
        Pose transform;
        if (isRelative) {
            Pose parentPose = parent.getPose();
            transform = pose.compose(parentPose);
        } else {
            transform = pose;
        }

        Anchor anchor = session.createAnchor(transform);
        return add(anchor, parent);
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

        Collection<Anchor> anchors = _subTreeElements(get(id), true);
        if (anchors == null) {
            return;
        }

        Pose transformation = pose;
        for (Anchor a : anchors) {
            if (local) {
                transformation = pose.compose(a.getPose());
            }
            a.detach();
            replace(a, session.createAnchor(transformation));
        }
    }
}
