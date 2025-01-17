package edu.technopark.arquest;

import android.graphics.Color;
import android.net.Uri;

import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.OmniLight;
import com.viro.core.PhysicsBody;
import com.viro.core.PhysicsShape;
import com.viro.core.PhysicsShapeSphere;
import com.viro.core.PhysicsWorld;
import com.viro.core.Texture;
import com.viro.core.Vector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
            player.setShape(new PhysicsShapeSphere(0.5f));
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
        unloadCurrentScene();
        if (player != null) {
            player.setPlace(null);
        }
    }

    public void setCurrentQuest(Quest quest) {
        if (quest == null || quest == this.currentQuest) {
            return;
        }
        this.currentQuest = quest;

        if (journals.getJournal(quest.getId()) == null) {
            journals.addJournal(quest.getId(), new Journal<String>());
        }

        if (inventories.getInventory(quest.getId()) == null && withAR) {
            inventories.addInventory(quest.getId(), new Slot(0, Player.INVENTORY, false));
        }
    }

    public ActorPlayer getPlayer() {
        return player;
    }

    public void takePlayerItem(Item item) {
        if (player == null) return;

        player.hold(item);
        scene.getRootNode().addChildNode(item);
    }

    public void releasePlayerItem() {
        if (player == null) return;

        Item item = player.getItem();
        if (item == null) return;

        item.removeFromParentNode();
        player.release();
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
        return player == null ? null : player.getPlace();
    }

    public void setCurrentPlace(Place place) {
        if (!withAR) {
            return;
        }
        if (player != null) player.setPlace(place);
        assetModule.loadPlace(place);
    }

    public ARScene getScene() {
        return scene;
    }

    public ARScene getNewFreeScene() {
        scene.dispose();
        scene = new ARScene();

        List<Vector> lightPositions = new ArrayList<Vector>();
        lightPositions.add(new Vector(-10,  10, 1));
        lightPositions.add(new Vector(10,  10, 1));

        float intensity = 300;
        List<Integer> lightColors = new ArrayList();
        lightColors.add(Color.WHITE);
        lightColors.add(Color.WHITE);

        for (int i = 0; i < lightPositions.size(); i++) {
            OmniLight light = new OmniLight();
            light.setColor(lightColors.get(i));
            light.setPosition(lightPositions.get(i));
            light.setAttenuationStartDistance(20);
            light.setAttenuationEndDistance(30);
            light.setIntensity(intensity);
            scene.getRootNode().addLight(light);
        }

        //Add an HDR environment map to give the Android's more interesting ambient lighting.
        Texture environment = Texture.loadRadianceHDRTexture(Uri.parse("file:///android_asset/ibl_newport_loft.hdr"));
        scene.setLightingEnvironment(environment);

        return scene;
    }

    public void unloadCurrentScene() {
        Place place = getCurrentPlace();
        if (place == null || scene == null) {
            return;
        }
        for (Object3D object3D : place.getAll()) {
            object3D.removeFromParentNode();
            object3D.clearPhysicsBody();
        }
        for (InteractiveObject obj : place.getInteractive()) {
            obj.setCurrentStateID(obj.getCurrentStateID());
        }
    }

    public void loadCurrentPlace(Vector origin) {
        Place place = getCurrentPlace();
        if (place == null || scene == null) {
            return;
        }
        Node root = scene.getRootNode();
        for (Object3D object3D : place.getAll()) {
            object3D.removeFromParentNode();

            PhysicsBody body = object3D.getPhysicsBody();
            if (body != null) {
                PhysicsBody.RigidBodyType type = body.getRigidBodyType();
                PhysicsShape shape = body.getShape();
                float mass = body.getMass();

                object3D.clearPhysicsBody();
                object3D.initPhysicsBody(type, mass, shape);
            }
            root.addChildNode(object3D);
        }

        for (InteractiveObject obj : place.getInteractive()) {
            obj.setCurrentStateID(obj.getCurrentStateID()); // init visual conditions
            // getLastSetPosition is used cos getPositionRealtime returns Vector(0, 0, 0)
            // unless object has already been rendered
            obj.setPosition(obj.getOriginalPosition().add(origin));
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
        EventBus.getDefault().post(lastCollision.object.interact(argument));
    }

    public void returnToInventory() {
        if (lastCollision == null) {
            return;
        }
        InteractionArgument argument;
        Item item = player.getItem();
        if (item != null) {
            argument = new InteractionArgument(null, Collections.singletonList(new Slot.RepeatedItem(item)));
            EventBus.getDefault().post(lastCollision.object.interact(argument));
        }
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
        player.updateOrientation(event.position, event.rotation, event.forward);
    }

    @Subscribe
    public void handleInteractionResults(final List<InteractionResult> results) {
        for (InteractionResult result : results) {
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
        releasePlayerItem();
    }

    private void onJournalUpdateResult(final InteractionResult result) {
        getCurrentJournal().addNow(result.getMsg());
    }
}
