package edu.technopark.arquest.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.technopark.arquest.game.InteractionResult;
import edu.technopark.arquest.game.InteractiveObject;
import edu.technopark.arquest.game.script.ActionCondition;
import edu.technopark.arquest.game.script.ObjectState;
import edu.technopark.arquest.game.script.ScriptAction;
import edu.technopark.arquest.game.slot.Slot;

public class PlaceConstructor {
    private String assetPrefix;

    public PlaceConstructor(String assetPrefix) {
        this.assetPrefix = assetPrefix;
    }

    private static void setContainerStates(InteractiveObject container, Slot.RepeatedItem item, InteractionResult... extraResults) {
        ObjectState state1 = new ObjectState(1, true);
        state1.setVisible(true);
        state1.setCollidable(true);

        List<InteractionResult> baseResults = new ArrayList<>();
        baseResults.add(InteractionResult.newItemsResult(item));
        baseResults.add(InteractionResult.transitionsResult(
                Collections.singletonList(
                        new ScriptAction.StateTransition(container.getName(), 2)
                )
        ));
        baseResults.addAll(Arrays.asList(extraResults));

        state1.setActions(
                Collections.singletonList(
                        new ScriptAction(1, baseResults)
                )
        );
        state1.setConditions(ActionCondition.makeConditionMap(
                Collections.singletonList(1),
                Collections.singletonList(
                        new ActionCondition(1)
                )
        ));

        ObjectState state2 = new ObjectState(2, false);
        state2.setVisible(false);
        state2.setCollidable(false);

        container.setStates(Arrays.asList(state1, state2));
        container.setAction(container.getActionFromStates());
    }

    private static void setAppearanceStates(InteractiveObject object) {
        ObjectState state1 = new ObjectState(1, true);
        state1.setVisible(false);
        state1.setCollidable(false);

        ObjectState state2 = new ObjectState(2, false);
        state2.setVisible(true);
        state2.setCollidable(false);

        object.setStates(Arrays.asList(state1, state2));
        object.setAction(object.getActionFromStates());
    }

    protected String asset(String name) {
        return assetPrefix + name;
    }
}
