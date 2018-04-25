package edu.technopark.arquest.game.script;

import edu.technopark.arquest.game.slot.Slot;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectState {
    public static ObjectState enableObjectState(int id, boolean isDefault, boolean isEnabled) {
        ObjectState result = new ObjectState(id, isDefault);
        result.setCollidable(true);
        result.setVisible(true);
        result.setEnabled(isEnabled);
        return result;
    }

    private int id;
    private boolean isDefault;
    private boolean enabled;
    private boolean visible;
    private boolean collidable;
    private Map<Integer, ScriptAction> actions;
    private Map<Integer, ActionCondition> conditions;

    public ObjectState(int id, boolean isDefault) {
        this.id = id;
        this.isDefault = isDefault;
        visible = true;
        collidable = true;
        enabled = true;
    }

    public ScriptAction getMatchingAction(Collection<Slot.RepeatedItem> items, List<String> strings) {
        int maxMatchRate = -1;
        ScriptAction result = null;

        for (Map.Entry<Integer, ActionCondition> entry : conditions.entrySet()) {
            int matchRate = entry.getValue().check(items, strings, id);
            if (matchRate >= 0 && matchRate > maxMatchRate) {
                maxMatchRate = matchRate;
                result = actions.get(entry.getKey());
            }
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public Map<Integer, ScriptAction> getActions() {
        return actions;
    }

    public void setActions(List<ScriptAction> actions) {
        this.actions = new HashMap<>(actions.size());
        for (ScriptAction action : actions) {
            this.actions.put(action.getId(), action);
        }
    }

    public Map<Integer, ActionCondition> getConditions() {
        return conditions;
    }

    public void setConditions(Map<Integer, ActionCondition> conditions) {
        this.conditions = conditions;
    }
}
