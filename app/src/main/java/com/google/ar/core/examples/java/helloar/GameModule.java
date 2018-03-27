package com.google.ar.core.examples.java.helloar;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.ar.Scene;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.Player;
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;
import com.google.ar.core.examples.java.helloar.model.Quest;
import com.google.ar.core.examples.java.helloar.quest.game.ActorPlayer;
import com.google.ar.core.examples.java.helloar.storage.Inventories;
import com.google.ar.core.examples.java.helloar.storage.Journals;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GameModule {
    private Journals journals;
    private Inventories inventories;
    private ActorPlayer player;
    private Scene scene;
    private Quest currentQuest;

    public GameModule() {
        journals = new Journals();
        inventories = new Inventories();

        player = new ActorPlayer(Pose.makeTranslation(0, 0, -0.3f));
        player.setCollider(new Collider(new Sphere(0.05f)));
        scene = new Scene();
    }

    @Provides
    @Singleton
    public GameModule provideGameModule() {
        return new GameModule();
    }

    public Quest getCurrentQuest() {
        return currentQuest;
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
        return journals.getJournal(currentQuest.getId());
    }

    public Slot getCurrentInventory() {
        return inventories.getInventory(currentQuest.getId());
    }

    public Place getCurrentPlace() {
        return player.getPlace();
    }

    public void setCurrentPlace(Place place) {
        player.setPlace(place);
    }

    public Scene getScene() {
        return scene;
    }
}
