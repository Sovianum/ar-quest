package com.google.ar.core.examples.java.helloar;


import com.google.ar.core.examples.java.helloar.auth.LoginFragment;
import com.google.ar.core.examples.java.helloar.auth.RegistrationFragment;
import com.google.ar.core.examples.java.helloar.network.NetworkModule;
import com.google.ar.core.examples.java.helloar.quest.ARFragment;
import com.google.ar.core.examples.java.helloar.quest.QuestFragment;
import com.google.ar.core.examples.java.helloar.quest.game.InteractionResultHandler;
import com.google.ar.core.examples.java.helloar.quest.game.QuestModule;
import com.google.ar.core.examples.java.helloar.quest.items.ItemsListFragment;
import com.google.ar.core.examples.java.helloar.quest.journal.JournalFragment;
import com.google.ar.core.examples.java.helloar.quest.journal.JournalMessageAdapter;
import com.google.ar.core.examples.java.helloar.quest.place.PlaceFragment;
import com.google.ar.core.examples.java.helloar.quest.place.PlacesAdapter;
import com.google.ar.core.examples.java.helloar.quest.quests.QuestsListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {GameModule.class, QuestModule.class, NetworkModule.class, ContextModule.class})
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
