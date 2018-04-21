package edu.technopark.arquest.common;


public class ContinuousAction {
    Runnable startAction;
    Runnable stopAction;

    boolean running;
    boolean started;

    public ContinuousAction(Runnable startAction, Runnable stopAction) {
        this.startAction = startAction;
        this.stopAction = stopAction;
        running = false;
        started = false;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isStarted() {
        return started;
    }

    public void start() {
        startAction.run();
        running = true;
        started = false;
    }
    
    public void stop() {
        stopAction.run();
        running = false;
    }
    
    public void startIfNotRunning() {
        if (!running) {
            startAction.run();
        }
        started = true;
        running = true;
    }

    public void stopIfRunning() {
        if (running) {
            stopAction.run();
        }
        running= false;
    }
}
