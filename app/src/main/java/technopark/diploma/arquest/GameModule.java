package technopark.diploma.arquest;

import com.google.ar.core.Pose;
import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import technopark.diploma.arquest.core.ar.Scene;
import technopark.diploma.arquest.core.ar.collision.Collider;
import technopark.diploma.arquest.core.ar.collision.shape.Sphere;
import technopark.diploma.arquest.core.game.Place;
import technopark.diploma.arquest.core.game.Player;
import technopark.diploma.arquest.core.game.journal.Journal;
import technopark.diploma.arquest.core.game.slot.Slot;
import technopark.diploma.arquest.model.Quest;
import technopark.diploma.arquest.quest.game.ActorPlayer;
import technopark.diploma.arquest.storage.Inventories;
import technopark.diploma.arquest.storage.Journals;
import technopark.diploma.arquest.storage.fs.FileModule;
import technopark.diploma.arquest.storage.fs.QuestDir;

@Module
public class GameModule {
    private Journals journals;
    private Inventories inventories;
    private ActorPlayer player;
    private Scene scene;
    private Quest currentQuest;

    @Inject
    Gson gson;

    @Inject
    FileModule fileModule;

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
        App.getAppComponent().inject(this);
        return this;
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
            journals.setJournal(currentQuest.getId(), new Journal<String>());
        }

        if (inventories.getInventory(currentQuest.getId()) == null) {
            inventories.setInventory(currentQuest.getId(), new Slot(0, Player.INVENTORY, false));
        }
    }

    public boolean saveCurrentState() throws IOException {
        QuestDir questDir = fileModule.getQuestDir(currentQuest.getId());
        questDir.saveJournal(getCurrentJournal(), gson);
        questDir.saveInventory(getCurrentInventory(), gson);
        questDir.saveQuestState(currentQuest, gson);
        questDir.savePlayerState(player, gson);
        return true;
    }

    public boolean loadQuestState(int questID) throws IOException {
        QuestDir questDir = fileModule.getQuestDir(questID);
        Journal<String> journalState = questDir.loadJournal(gson);
        if (journalState == null) {
            return false;
        }

        Slot inventoryState = questDir.loadInventory(gson);
        if (inventoryState == null) {
            return false;
        }

        Quest questState = questDir.loadQuestState(gson);
        if (questState == null) {
            return false;
        }

        ActorPlayer playerState = questDir.loadActorPlayer(gson);
        if (playerState == null) {
            return false;
        }

        journals.setJournal(questID, journalState);
        inventories.setInventory(questID, inventoryState);
        player = playerState;
        currentQuest = questState;
        return true;
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
