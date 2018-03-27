package com.google.ar.core.examples.java.helloar;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.ar.Scene;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;
import com.google.ar.core.examples.java.helloar.quest.game.ActorPlayer;
import com.google.ar.core.examples.java.helloar.quest.place.Places;
import com.google.ar.core.examples.java.helloar.storage.PlacesStorage;
import com.google.ar.core.examples.java.helloar.storage.Inventories;
import com.google.ar.core.examples.java.helloar.storage.Journals;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GameModule {
    private Journals journals;
    private Inventories inventories;
    private PlacesStorage placesStorage;
    private Integer currentQuestId;
    private ActorPlayer player;
    private Scene scene;

    public GameModule() {
        journals = new Journals();
        inventories = new Inventories();
        placesStorage = new PlacesStorage();

        player = new ActorPlayer(Pose.makeTranslation(0, 0, -0.3f));
        player.setCollider(new Collider(new Sphere(0.05f)));
        scene = new Scene();
    }

    @Provides
    @Singleton
    public GameModule provideGameModule() {
        return new GameModule();
    }

    public ActorPlayer getPlayer() {
        return player;
    }

    public Journals getJournals() {
        return journals;
    }

    public Inventories getInventories() {
        return inventories;
    }

    public PlacesStorage getPlacesStorage() {
        return placesStorage;
    }

    public void setCurrentQuestId(Integer id) {
        currentQuestId = id;
    }

    public void addCurrentJournal(Journal<String> journal) {
        journals.addJournal(getCurrentQuestId(), journal);
    }

    public Journal<String> getCurrentJournal() {
        return journals.getJournal(getCurrentQuestId());
    }

    public void addCurrentInventory(Slot inventory) {
        inventories.addInventory(getCurrentQuestId(), inventory);
    }

    public Slot getCurrentInventory() {
        return inventories.getInventory(getCurrentQuestId());
    }

    public void addCurrentPlaces(Places places) {
        placesStorage.appPlaces(getCurrentQuestId(), places);
    }

    public Places getCurrentPlaces() {
        return placesStorage.getPlaces(getCurrentQuestId());
    }

    public Place getCurrentPlace() {
        return player.getPlace();
    }

    public void setCurrentPlace(Place place) {
        player.setPlace(place);

    }

    public Integer getCurrentQuestId() {
        return currentQuestId;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
