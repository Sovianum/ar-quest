package com.google.ar.core.examples.java.helloar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.examples.java.helloar.auth.AuthActivity;
import com.google.ar.core.examples.java.helloar.core.ar.Scene;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.Player;
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;
import com.google.ar.core.examples.java.helloar.model.Quest;
import com.google.ar.core.examples.java.helloar.network.NetworkModule;
import com.google.ar.core.examples.java.helloar.quest.ARFragment;
import com.google.ar.core.examples.java.helloar.quest.QuestFragment;
import com.google.ar.core.examples.java.helloar.quest.game.QuestModule;
import com.google.ar.core.examples.java.helloar.quest.items.ItemAdapter;
import com.google.ar.core.examples.java.helloar.quest.items.ItemsListFragment;
import com.google.ar.core.examples.java.helloar.quest.journal.JournalFragment;
import com.google.ar.core.examples.java.helloar.quest.place.PlaceFragment;
import com.google.ar.core.examples.java.helloar.quest.place.Places;
import com.google.ar.core.examples.java.helloar.quest.quests.QuestAdapter;
import com.google.ar.core.examples.java.helloar.quest.quests.QuestsListFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.ar_fragment_btn)
    Button toQuestFragmentButton;

    @BindView(R.id.auth_activity_btn)
    Button toAuthActivityButton;

    private QuestsListFragment questsListFragment;
    private ARFragment arFragment;
    private QuestFragment questFragment;
    private ItemsListFragment itemsListFragment;
    private JournalFragment journalFragment;
    private PlaceFragment placeFragment;

    @Inject
    GameModule gameModule;

    @Inject
    QuestModule questModule;

    @Inject
    NetworkModule networkModule;

    private QuestAdapter.OnItemClickListener toQuestItemOnClickListener = new QuestAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Quest item) {
            selectFragment(questFragment, questFragment.TAG);
        }
    };

    private ItemAdapter.OnItemClickListener chooseItemOnClickListener = new ItemAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Item item) {
            gameModule.getPlayer().hold(item);
            Toast.makeText(MainActivity.this, "You selected: " + item.getName(), Toast.LENGTH_SHORT).show();
            //TODO action to choose element
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // the order of calls below is important
        setUpArFragment();
        setUpGameModule();
        setUpQuestFragment();

        questsListFragment = new QuestsListFragment();
        questsListFragment.setOnItemClickListener(toQuestItemOnClickListener);

        toQuestFragmentButton.setOnClickListener(getSelectFragmentListener(arFragment));
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuthorization();
        selectFragment(questsListFragment, questsListFragment.TAG);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void setUpQuestFragment() {
        placeFragment = new PlaceFragment();

        questFragment = new QuestFragment();
        questFragment.setOnARModeBtnClickListener(getSelectFragmentListener(arFragment));
        questFragment.setOnJournalClickListener(getSelectFragmentListener(journalFragment));
        questFragment.setOnItemClickListener(chooseItemOnClickListener);
        questFragment.setOnPlacesClickListener(getSelectFragmentListener(placeFragment));
    }

    private void setUpGameModule() {
        Integer questId = 1;
        gameModule.setCurrentQuestId(questId);

        Journal<String> journal = new Journal<>();
        journal.addNow("First record");
        journal.addNow("Second record");
        journal.addNow("Third record");
        gameModule.addCurrentJournal(journal);

        gameModule.addCurrentInventory(new Slot(0, Player.INVENTORY, false));

        Places places = new Places();
        places.addCheckpoint(new Place(0, "First place", "Description")); //STUB!!!
        gameModule.addCurrentPlaces(places);
    }

    private void setUpArFragment() {
        journalFragment = new JournalFragment();

        itemsListFragment = new ItemsListFragment();
        itemsListFragment.setOnItemClickListener(chooseItemOnClickListener);

        Scene scene = new Scene();
        Place place = questModule.getInteractionDemoPlace();

        arFragment = new ARFragment();
        arFragment.setToInventoryOnClickListener(getSelectFragmentListener(itemsListFragment));
        arFragment.setToJournalOnClickListener(getSelectFragmentListener(journalFragment));

        arFragment.setDecorations(scene, place);
    }

    @OnClick(R.id.auth_activity_btn)
    public void goToAuthActivity(View v) {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    private <F extends Fragment> View.OnClickListener getSelectFragmentListener(final F fragment) {
        if (fragment == null) {
            throw new RuntimeException("tried to register null fragment");
        }
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFragment(fragment, fragment.getClass().getSimpleName());
            }
        };
    }

    private void selectFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;

        boolean needPut = true;
        boolean needRemove = false;

        Fragment lastFragment = null;
        if (index >= 0) {
            FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(index);
            String lastTag = backEntry.getName();
            lastFragment = fragmentManager.findFragmentByTag(lastTag);

            if (lastFragment == fragment) {
                needPut = false;
                needRemove = false;
            } else {
                needPut = true;
                needRemove = true;
            }
        }

        if (needPut || needRemove) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (needRemove && lastFragment != null) {
                fragmentTransaction.remove(lastFragment);
            }
            fragmentTransaction.add(R.id.main_fragment_container, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        }


    }

    private void checkAuthorization() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String jwt = prefs.getString(getResources().getString(R.string.json_web_token), null);

        if (jwt == null) {
            Intent intentAuth = new Intent(this, AuthActivity.class);
            startActivity(intentAuth);
        } else {
            networkModule.setToken(jwt);
        }
    }

    private void removeToken() {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).edit();

        editor.putString(getResources().getString(R.string.json_web_token), null);
        editor.apply();
    }
}
