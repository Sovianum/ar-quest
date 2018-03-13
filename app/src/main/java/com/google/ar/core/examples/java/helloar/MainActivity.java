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

import com.google.ar.core.examples.java.helloar.auth.AuthActivity;
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.model.Inventory;
import com.google.ar.core.examples.java.helloar.model.Item;
import com.google.ar.core.examples.java.helloar.model.Quest;
import com.google.ar.core.examples.java.helloar.network.Api;
import com.google.ar.core.examples.java.helloar.quest.ARFragment;
import com.google.ar.core.examples.java.helloar.quest.QuestFragment;
import com.google.ar.core.examples.java.helloar.quest.items.ItemAdapter;
import com.google.ar.core.examples.java.helloar.quest.items.ItemsListFragment;
import com.google.ar.core.examples.java.helloar.quest.journal.JournalFragment;
import com.google.ar.core.examples.java.helloar.quest.quests.QuestAdapter;
import com.google.ar.core.examples.java.helloar.quest.quests.QuestsListFragment;
import com.google.ar.core.examples.java.helloar.storage.Inventories;

public class MainActivity extends AppCompatActivity {
    private Button toQuestFragmentButton;
    private Button toAuthActivityButton;

    private QuestsListFragment questsListFragment;
    private ARFragment arFragment;
    private QuestFragment questFragment;
    private ItemsListFragment itemsListFragment;
    private JournalFragment journalFragment;

    private Fragment activeFragment;

    private Journal<String> journal;
    private Inventories inventories;

    private QuestAdapter.OnItemClickListener toQuestItemOnClickListener = new QuestAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Quest item) {
            selectFragment(questFragment);
        }
    };

    private ItemAdapter.OnItemClickListener chooseItemOnClickListener = new ItemAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Item item) {
            System.out.println("You chose + " + item.getName());
            //TODO action to choose element
        }
    };

    private View.OnClickListener toInventoryOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectFragment(itemsListFragment);
        }
    };

    private View.OnClickListener toJournalOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectFragment(journalFragment);
        }
    };

    private View.OnClickListener toAROnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(arFragment);
        }
    };

    private View.OnClickListener toJournalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(journalFragment);
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
        Api.setCurrentQuestId(questId);
        Api.getJournals().addCurrentJournal(journal);
        Inventory inventory = new Inventory();
        inventory.addItem(new Item("Меч", "Большой и страшный меч", ""));
        inventory.addItem(new Item("Щит", "Маленький и забавный щит", ""));
        Api.getInventories().addCurrentInventory(inventory);

        //removeToken(); //debug

        questFragment = new QuestFragment();
        questFragment.setOnARModeBtnClickListener(toAROnClickListener);
        questFragment.setOnJournalClickListener(toJournalClickListener);
        //questFragment.setJournal(journal);
        questFragment.setOnItemClickListener(chooseItemOnClickListener);

        questsListFragment = new QuestsListFragment();
        questsListFragment.setOnItemClickListener(toQuestItemOnClickListener);

        itemsListFragment = new ItemsListFragment();
        itemsListFragment.setOnItemClickListener(chooseItemOnClickListener);

        arFragment = new ARFragment();
        arFragment.setToInventoryOnClickListener(toInventoryOnClickListener);
        arFragment.setToJournalOnClickListener(toJournalOnClickListener);

        journalFragment = new JournalFragment();

        toQuestFragmentButton = findViewById(R.id.ar_fragment_btn);
        toAuthActivityButton = findViewById(R.id.auth_activity_btn);
        toQuestFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(arFragment);
            }
        });

        toAuthActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAuthActivity(view);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuthorization();
        selectFragment(questsListFragment);
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

    private void selectFragment(Fragment fragment) {
        if (fragment == activeFragment) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (activeFragment != null) {
            fragmentTransaction.remove(activeFragment);
        }
        activeFragment = fragment;
        fragmentTransaction.add(R.id.main_fragment_container, activeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
