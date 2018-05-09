package edu.technopark.arquest.quest.game;

import com.viro.core.AnimationTransaction;
import com.viro.core.Node;
import com.viro.core.Vector;

import java.util.ArrayDeque;
import java.util.Queue;

public class FlyingAnimator {
    private class Orientation {
        Vector position;
        Vector rotation;
        Vector forward;

        public Orientation(Vector position, Vector rotation, Vector forward) {
            this.position = position;
            this.rotation = rotation;
            this.forward = forward;
        }

        public Vector getForwardDisplacedPosition(float displace) {
            return position.add(forward.normalize().scale(displace));
        }
    }

    private long lastTime;

    private Queue<Long> timeQueue;
    private Queue<Orientation> orientationQueue;
    private Node animatedNode;

    private boolean animatingPast = false;

    AnimationTransaction.Listener trackerListener = new AnimationTransaction.Listener() {
        @Override
        public void onFinish(AnimationTransaction animationTransaction) {
            if (orientationQueue.peek() != null) {
                animatePast();
            } else {
                animatingPast = false;
            }
        }
    };

    public FlyingAnimator(Node animatedNode) {
        lastTime = 0;
        this.animatedNode = animatedNode;
        timeQueue = new ArrayDeque<>();
        orientationQueue = new ArrayDeque<>();
    }

    public void addCheckPoint(Vector position, Vector rotation, Vector forward) {
        if (animatedNode == null) {
            timeQueue.clear();
            orientationQueue.clear();
            lastTime = 0;
            return;
        }
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
            return;
        }

        long time = System.currentTimeMillis();
        timeQueue.add(time);
        orientationQueue.add(new Orientation(position, rotation, forward));

        if (!animatingPast) {
            animatePast();
        }
    }

    public void setAnimatedNode(Node animatedNode) {
        this.animatedNode = animatedNode;
    }

    private void animatePast() {
        if (animatedNode == null) {
            animatingPast = false;
            return;
        }
        animatingPast = true;
        Orientation newOrientation = orientationQueue.poll();
        long newTime = timeQueue.poll();
        long timeDelta = (newTime - lastTime) / 5;
        lastTime = newTime;

        AnimationTransaction.begin();
        AnimationTransaction.setAnimationDuration(timeDelta);
        animatedNode.setPosition(newOrientation.getForwardDisplacedPosition(0.2f));
        animatedNode.setRotation(newOrientation.rotation);
        AnimationTransaction.setListener(trackerListener);
        AnimationTransaction.commit();
    }
}
