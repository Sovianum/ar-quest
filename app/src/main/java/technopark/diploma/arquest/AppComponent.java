package technopark.diploma.arquest;


import technopark.diploma.arquest.auth.LoginFragment;
import technopark.diploma.arquest.auth.RegistrationFragment;
import technopark.diploma.arquest.network.NetworkModule;
import technopark.diploma.arquest.quest.ARFragment;
import technopark.diploma.arquest.quest.QuestFragment;
import technopark.diploma.arquest.quest.game.InteractionResultHandler;
import technopark.diploma.arquest.quest.game.QuestModule;
import technopark.diploma.arquest.quest.items.ItemsListFragment;
import technopark.diploma.arquest.quest.journal.JournalFragment;
import technopark.diploma.arquest.quest.journal.JournalMessageAdapter;
import technopark.diploma.arquest.quest.place.PlaceFragment;
import technopark.diploma.arquest.quest.place.PlacesAdapter;
import technopark.diploma.arquest.quest.quests.QuestsListFragment;
import technopark.diploma.arquest.storage.fs.FileModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        GameModule.class, QuestModule.class,
        NetworkModule.class, ContextModule.class,
        CommonModule.class, FileModule.class,
})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(ARFragment fragment);
    void inject(QuestFragment fragment);
    void inject(QuestsListFragment fragment);
    void inject(InteractionResultHandler handler);
    void inject(QuestModule service);
    void inject(ItemsListFragment fragment);
    void inject(JournalFragment fragment);
    void inject(JournalMessageAdapter adapter);
    void inject(PlaceFragment placeFragment);
    void inject(PlacesAdapter adapter);
    void inject(LoginFragment fragment);
    void inject(RegistrationFragment fragment);
    void inject(GeolocationService service);
}
