package edu.technopark.arquest.game.script;


import edu.technopark.arquest.game.InteractionResult;

import java.util.Collection;

public class ScriptAction {
    public static class StateTransition {
        private String targetObjectName;
        private int targetStateID;

        public StateTransition(String targetObjectName, int targetStateID) {
            this.targetObjectName = targetObjectName;
            this.targetStateID = targetStateID;
        }

        public String getTargetObjectName() {
            return targetObjectName;
        }

        public int getTargetStateID() {
            return targetStateID;
        }
    }

    private int id;
    private Collection<InteractionResult> interactionResults;

    public ScriptAction(int id, Collection<InteractionResult> interactionResults) {
        this.id = id;
        this.interactionResults = interactionResults;
    }

    public int getId() {
        return id;
    }

    public Collection<InteractionResult> getInteractionResults() {
        return interactionResults;
    }
}
