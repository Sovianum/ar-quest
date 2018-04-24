package edu.technopark.arquest;

import com.viro.core.ARScene;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.PhysicsShapeSphere;
import com.viro.core.PhysicsWorld;
import com.viro.core.Vector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.technopark.arquest.game.InteractionArgument;
import edu.technopark.arquest.game.InteractionResult;
import edu.technopark.arquest.game.InteractiveObject;
import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.Player;
import edu.technopark.arquest.game.journal.Journal;
import edu.technopark.arquest.game.script.ScriptAction;
import edu.technopark.arquest.game.slot.Slot;
import edu.technopark.arquest.model.Quest;
import edu.technopark.arquest.quest.ARActivity;
import edu.technopark.arquest.quest.AssetModule;
import edu.technopark.arquest.quest.game.ActorPlayer;
import edu.technopark.arquest.storage.Inventories;
import edu.technopark.arquest.storage.Journals;

@Module
public class GameModule {
    public static final String PLAYER_COLLISION_TAG = "PLAYER_COLLISION";
    public static class CanInteract{
        public boolean canInteract;

        public CanInteract(boolean canInteract) {
            this.canInteract = canInteract;
        }
    }

    private Journals journals;
    private Inventories inventories;
    private ActorPlayer player;
    private ARScene scene;
    private Quest currentQuest;
    private boolean withAR;
    private Map<String, InteractiveObject.InteractiveObjectCollisionEvent> collisionMap;
    private InteractiveObject.InteractiveObjectCollisionEvent lastCollision;

    @Inject
    AssetModule assetModule;

    public GameModule(boolean withAR) {
        journals = new Journals();
        inventories = new Inventories();
        this.withAR = withAR;

        if (withAR) {
            // load native libraries
            System.loadLibrary("gvr");
            System.loadLibrary("gvr_audio");
            System.loadLibrary("viro_renderer");
            System.loadLibrary("viro_arcore");

            scene = new ARScene();
            scene.getPhysicsWorld().setGravity(new Vector(0,0, 0));
            player = new ActorPlayer();
            player.setShape(new PhysicsShapeSphere(0.3f));
            scene.getRootNode().addChildNode(player);
            collisionMap = new HashMap<>();
        }
        EventBus.getDefault().register(this);
    }

    @Provides
    @Singleton
    public GameModule provideGameModule() {
        App.getAppComponent().inject(this);
        return this;
    }

    public boolean isWithAR() {
        return withAR;
    }

    public Quest getCurrentQuest() {
        return currentQuest;
    }

    public void resetCurrentQuest() {
        currentQuest = null;
    }

    public void setCurrentQuest(Quest currentQuest) {
        if (currentQuest == null || currentQuest == this.currentQuest) {
            return;
        }
        this.currentQuest = currentQuest;

        if (journals.getJournal(currentQuest.getId()) == null) {
            journals.addJournal(currentQuest.getId(), new Journal<String>());
        }

        if (inventories.getInventory(currentQuest.getId()) == null && withAR) {
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
        if (!withAR) {
            return;
        }
        if (player != null) player.setPlace(place);
        assetModule.loadPlace(place);
    }

    public ARScene getNewScene() {
        scene = new ARScene();
        loadCurrentPlace();
        return scene;
    }

    public void loadCurrentPlace() {
        Place place = getCurrentPlace();
        if (place == null || scene == null) {
            return;
        }
        Node root = scene.getRootNode();
        for (Object3D object3D : place.getAll()) {
            root.addChildNode(object3D);
        }

        for (InteractiveObject obj : place.getInteractive()) {
            if (obj.getPhysicsBody() != null) {
                obj.setDefaultCollisionListener();
            }
        }
    }

    public void interactLastCollided() {
        if (lastCollision == null) {
            return;
        }
        InteractionArgument argument;
        Item item = player.getItem();
        if (item != null) {
            argument = new InteractionArgument(null, Collections.singletonList(new Slot.RepeatedItem(item)));
        } else {
            argument = new InteractionArgument(null, null);
        }
        for (InteractionResult result : lastCollision.object.interact(argument)) {
            EventBus.getDefault().post(result);
        }

//        InteractiveObject closest = null;
//        float minDistance = 1e10f;
//
//        Vector playerPosition = player.getPositionRealtime();
//        for (InteractiveObject.InteractiveObjectCollisionEvent event : collisionMap.values()) {
//            float distance = event.position.distance(playerPosition);
//            if (distance <= player.getAccessRange() && distance < minDistance) {
//                minDistance = distance;
//                closest = event.object;
//            }
//        }
//
//        if (closest != null) {
//            InteractionArgument argument;
//            Item item = player.getItem();
//            if (item != null) {
//                argument = new InteractionArgument(null, Collections.singletonList(new Slot.RepeatedItem(item)));
//            } else {
//                argument = new InteractionArgument(null, null);
//            }
//            Collection<InteractionResult> results = closest.interact(argument);
//
//            for (InteractionResult result : results) {
//                EventBus.getDefault().post(result);
//            }
//        }
    }

    @Subscribe
    public void handleInteractiveObjectCollisionEvent(final InteractiveObject.InteractiveObjectCollisionEvent event) {
        collisionMap.put(event.object.getName(), event);
        lastCollision = event;
    }

    @Subscribe
    public void handleCameraUpdateEvent(final ARActivity.CameraUpdateEvent event) {
        scene.getPhysicsWorld().findCollisionsWithShapeAsync(
                event.position, event.position, player.getShape(),
                PLAYER_COLLISION_TAG, new PhysicsWorld.HitTestListener() {
                    @Override
                    public void onComplete(boolean b) {
                        EventBus.getDefault().post(new CanInteract(b));
                        if (!b) {
                            lastCollision = null;
                        }
                    }
                }
        );
        player.setPosition(event.position);
        player.setRotation(event.rotation);
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
                    .get(transition.getTargetObjectName())
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
