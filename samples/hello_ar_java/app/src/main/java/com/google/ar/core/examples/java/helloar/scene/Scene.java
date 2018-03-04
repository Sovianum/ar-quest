package com.google.ar.core.examples.java.helloar.scene;

import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;

import java.util.Collection;

public class Scene extends SceneTree<Anchor> {
    private Session session;

    public Scene(Session session) {
        super();
        this.session = session;
    }

    public void clear() {
        for (Anchor a : super.all()) {
            a.detach();
        }
        super.clear();
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
            transform = pose.compose(parent.getPose());
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
