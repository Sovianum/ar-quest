package technopark.diploma.arquest;

import com.google.ar.core.Pose;
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
