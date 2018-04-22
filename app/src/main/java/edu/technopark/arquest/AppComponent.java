package edu.technopark.arquest;


import edu.technopark.arquest.auth.LoginFragment;
import edu.technopark.arquest.auth.RegistrationFragment;
import edu.technopark.arquest.network.NetworkModule;
import edu.technopark.arquest.quest.ARActivity;
import edu.technopark.arquest.quest.AssetModule;
import edu.technopark.arquest.quest.QuestFragment;
import edu.technopark.arquest.quest.game.QuestModule;
import edu.technopark.arquest.quest.items.ItemAdapter;
import edu.technopark.arquest.quest.items.ItemsListFragment;
import edu.technopark.arquest.quest.journal.JournalFragment;
import edu.technopark.arquest.quest.journal.JournalMessageAdapter;
import edu.technopark.arquest.quest.place.PlaceFragment;
import edu.technopark.arquest.quest.place.PlacesAdapter;
import edu.technopark.arquest.quest.quests.QuestAdapter;
import edu.technopark.arquest.quest.quests.QuestsListFragment;
import edu.technopark.arquest.settings.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {GameModule.class, QuestModule.class, NetworkModule.class, ContextModule.class, HintModule.class, AssetModule.class})
public interface AppComponent {
    void inject(GameModule module);
    void inject(AssetModule module);
    void inject(HintModule module);
    void inject(ItemAdapter itemAdapter);
    void inject(QuestAdapter adapter);
    void inject(MainActivity activity);
    void inject(ARActivity fragment);
    void inject(QuestFragment fragment);
    void inject(QuestsListFragment fragment);
    void inject(QuestModule service);
    void inject(ItemsListFragment fragment);
    void inject(JournalFragment fragment);
    void inject(JournalMessageAdapter adapter);
    void inject(PlaceFragment placeFragment);
    void inject(PlacesAdapter adapter);
    void inject(LoginFragment fragment);
    void inject(RegistrationFragment fragment);
    void inject(SettingsFragment fragment);
    void inject(GeolocationService service);
}
