package edu.technopark.arquest.core.game.script;


import edu.technopark.arquest.core.game.InteractionResult;

import java.util.Collection;

public class ScriptAction {
    public static class StateTransition {
        private int targetObjectID;
        private int targetStateID;

        public StateTransition(int targetObjectID, int targetStateID) {
            this.targetObjectID = targetObjectID;
            this.targetStateID = targetStateID;
        }

        public int getTargetObjectID() {
            return targetObjectID;
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
