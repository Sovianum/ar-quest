package com.google.ar.core.examples.java.helloar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.auth.AuthActivity;
import com.google.ar.core.examples.java.helloar.core.ar.Scene;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.Player;
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;
import com.google.ar.core.examples.java.helloar.model.Quest;
import com.google.ar.core.examples.java.helloar.network.Api;
import com.google.ar.core.examples.java.helloar.quest.ARFragment;
import com.google.ar.core.examples.java.helloar.quest.QuestFragment;
import com.google.ar.core.examples.java.helloar.quest.game.ActorPlayer;
import com.google.ar.core.examples.java.helloar.quest.game.QuestService;
import com.google.ar.core.examples.java.helloar.quest.items.ItemAdapter;
import com.google.ar.core.examples.java.helloar.quest.items.ItemsListFragment;
import com.google.ar.core.examples.java.helloar.quest.journal.JournalFragment;
import com.google.ar.core.examples.java.helloar.quest.place.PlaceFragment;
import com.google.ar.core.examples.java.helloar.quest.place.Places;
import com.google.ar.core.examples.java.helloar.quest.quests.QuestAdapter;
import com.google.ar.core.examples.java.helloar.quest.quests.QuestsListFragment;

public class MainActivity extends AppCompatActivity {
    private Button toQuestFragmentButton;
    private Button toAuthActivityButton;

    private QuestsListFragment questsListFragment;
    private ARFragment arFragment;
    private QuestFragment questFragment;
    private ItemsListFragment itemsListFragment;
    private JournalFragment journalFragment;
    private PlaceFragment placeFragment;
    private ActorPlayer player;

    private Journal<String> journal;

    private Scene scene;
    private Place place;

    private QuestAdapter.OnItemClickListener toQuestItemOnClickListener = new QuestAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Quest item) {
            selectFragment(questFragment, questFragment.TAG);
        }
    };

    private ItemAdapter.OnItemClickListener chooseItemOnClickListener = new ItemAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Item item) {
            player.hold(item);
            Toast.makeText(MainActivity.this, "You selected: " + item.getName(), Toast.LENGTH_SHORT).show();
            //TODO action to choose element
        }
    };

    private View.OnClickListener toInventoryOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectFragment(itemsListFragment, itemsListFragment.TAG);
        }
    };

    private View.OnClickListener toJournalOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectFragment(journalFragment, journalFragment.TAG);
        }
    };

    private View.OnClickListener toAROnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(arFragment, arFragment.TAG);
        }
    };

    private View.OnClickListener toJournalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(journalFragment, journalFragment.TAG);
        }
    };

    private View.OnClickListener toPlacesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(placeFragment, placeFragment.TAG);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        journal = new Journal<>();
        journal.addNow("First record");
        journal.addNow("Second record");
        journal.addNow("Third record");
        Integer questId = 1;

        GameApi.setCurrentQuestId(questId);
        GameApi.getJournals().addCurrentJournal(journal);
        GameApi.getInventories().addCurrentInventory(new Slot(0, Player.INVENTORY, false));
        Places places = new Places();
        places.addCheckpoint(new Place(0, "First place", "Description")); //STUB!!!
        GameApi.getPlacesStorage().addCurrentPlaces(places);

        //removeToken(); //debug

        questFragment = new QuestFragment();
        questFragment.setOnARModeBtnClickListener(toAROnClickListener);
        questFragment.setOnJournalClickListener(toJournalClickListener);
        //questFragment.setJournal(journal);
        questFragment.setOnItemClickListener(chooseItemOnClickListener);

        questFragment.setOnPlacesClickListener(toPlacesClickListener);

        questsListFragment = new QuestsListFragment();
        questsListFragment.setOnItemClickListener(toQuestItemOnClickListener);

        itemsListFragment = new ItemsListFragment();
        itemsListFragment.setOnItemClickListener(chooseItemOnClickListener);

        arFragment = new ARFragment();
        arFragment.setToInventoryOnClickListener(toInventoryOnClickListener);
        arFragment.setToJournalOnClickListener(toJournalOnClickListener);

        journalFragment = new JournalFragment();

        placeFragment = new PlaceFragment();

        toQuestFragmentButton = findViewById(R.id.ar_fragment_btn);
        toAuthActivityButton = findViewById(R.id.auth_activity_btn);
        toQuestFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(arFragment, arFragment.TAG);
            }
        });

        toAuthActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAuthActivity(view);
            }
        });

        player = new ActorPlayer(Pose.makeTranslation(0, 0, -0.3f));
        player.setCollider(new Collider(new Sphere(0.05f)));
        arFragment.setPlayer(player);

        scene = new Scene();
        place = QuestService.getInteractionDemoPlace();
        arFragment.setDecorations(scene, place);
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

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        return super.onCreateView(parent, name, context, attrs);
    }

    private void goToAuthActivity(View v) {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
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
            Api.getInstance().setToken(jwt);
        }
    }

    private void removeToken() {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).edit();

        editor.putString(getResources().getString(R.string.json_web_token), null);
        editor.apply();
    }
}
