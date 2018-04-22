package edu.technopark.arquest;

import com.viro.core.ARScene;
import com.viro.core.PhysicsBody;
import com.viro.core.PhysicsShapeSphere;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.technopark.arquest.game.InteractionResult;
import edu.technopark.arquest.game.InteractiveObject;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.Player;
import edu.technopark.arquest.game.journal.Journal;
import edu.technopark.arquest.game.script.ScriptAction;
import edu.technopark.arquest.game.slot.Slot;
import edu.technopark.arquest.model.Quest;
import edu.technopark.arquest.quest.game.ActorPlayer;
import edu.technopark.arquest.storage.Inventories;
import edu.technopark.arquest.storage.Journals;

@Module
public class GameModule {
    private Journals journals;
    private Inventories inventories;
    private ActorPlayer player;
    private ARScene scene;
    private Quest currentQuest;

    public GameModule() {
        // load native libraries
        System.loadLibrary("gvr");
        System.loadLibrary("gvr_audio");
        System.loadLibrary("viro_renderer");
        System.loadLibrary("viro_arcore");

        journals = new Journals();
        inventories = new Inventories();

        player = new ActorPlayer();
        player.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.05f));
        scene = new ARScene();

        EventBus.getDefault().register(this);
    }

    @Provides
    @Singleton
    public GameModule provideGameModule() {
        return new GameModule();
    }

    public Quest getCurrentQuest() {
        return currentQuest;
    }

    public void resetCurrentQuest() {
        currentQuest = null;
    }

    public void setCurrentQuest(Quest currentQuest) {
        // todo add state loading
        if (currentQuest == null || currentQuest == this.currentQuest) {
            return;
        }
        this.currentQuest = currentQuest;

        if (journals.getJournal(currentQuest.getId()) == null) {
            journals.addJournal(currentQuest.getId(), new Journal<String>());
        }

        if (inventories.getInventory(currentQuest.getId()) == null) {
            inventories.addInventory(currentQuest.getId(), new Slot(0, Player.INVENTORY, false));
        }
    }

    public ActorPlayer getPlayer() {
        return player;
    }

    public Journal<String> getCurrentJournal() {
        if (currentQuest == null) {
            return null;
        }
        return journals.getJournal(currentQuest.getId());
    }

    public Slot getCurrentInventory() {
        if (currentQuest == null) {
            return null;
        }
        return inventories.getInventory(currentQuest.getId());
    }

    public Place getCurrentPlace() {
        return player.getPlace();
    }

    public void setCurrentPlace(Place place) {
        player.setPlace(place);
    }

    public ARScene getScene() {
        return scene;
    }

    @Subscribe
    public void handleInteractionResult(final InteractionResult result) {
        switch (result.getType()) {
            case TRANSITIONS:
                onTransitionsResult(result);
                break;
            case NEW_ITEMS:
                onNewItemsResult(result);
                break;
            case TAKE_ITEMS:
                onTakeItemsResult(result);
                break;
            case JOURNAL_RECORD:
                onJournalUpdateResult(result);
                break;
        }
    }

    private void onTransitionsResult(final InteractionResult result) {
        Place currPlace = getPlayer().getPlace();
        Map<String, InteractiveObject> interactiveObjectMap = currPlace.getInteractiveObjects();
        for (ScriptAction.StateTransition transition : result.getTransitions()) {
            interactiveObjectMap
                    .get(transition.getTargetObjectID())
                    .setCurrentStateID(transition.getTargetStateID());
        }
    }

    private void onNewItemsResult(final InteractionResult result) {
        Slot.RepeatedItem repeatedItem = result.getItems();
        getCurrentInventory().put(repeatedItem);
    }

    private void onTakeItemsResult(final InteractionResult result) {
        Slot.RepeatedItem repeatedItem = result.getItems();
        getCurrentInventory().remove(repeatedItem.getItem().getId(), repeatedItem.getCnt());
        getPlayer().release();
    }

    private void onJournalUpdateResult(final InteractionResult result) {
        getCurrentJournal().addNow(result.getMsg());
    }
}
