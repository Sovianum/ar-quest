package technopark.diploma.arquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import technopark.diploma.arquest.auth.AuthActivity;
import technopark.diploma.arquest.core.game.Item;
import technopark.diploma.arquest.core.game.Place;
import technopark.diploma.arquest.core.game.journal.Journal;
import technopark.diploma.arquest.model.Quest;
import technopark.diploma.arquest.network.NetworkModule;
import technopark.diploma.arquest.quest.ARFragment;
import technopark.diploma.arquest.quest.QuestFragment;
import technopark.diploma.arquest.quest.game.QuestModule;
import technopark.diploma.arquest.quest.items.ItemAdapter;
import technopark.diploma.arquest.quest.items.ItemsListFragment;
import technopark.diploma.arquest.quest.journal.JournalFragment;
import technopark.diploma.arquest.quest.place.PlaceFragment;
import technopark.diploma.arquest.quest.quests.QuestsListFragment;

public class MainActivity extends AppCompatActivity {
    private LocationListener onLocationChangeListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

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

    private QuestsListFragment.OnQuestReactor showQuestInfoCallback = new QuestsListFragment.OnQuestReactor() {
        @Override
        public void onQuestReact(Quest quest) {
            selectFragment(questFragment, QuestFragment.TAG);
        }
    };

    private QuestsListFragment.OnQuestReactor startQuestCallback = new QuestsListFragment.OnQuestReactor() {
        @Override
        public void onQuestReact(final Quest quest) {
            try {
                gameModule.loadQuestState(quest.getId());
            } catch (IOException e) {
                e.printStackTrace();
                gameModule.setCurrentQuest(quest);
            }
            gameModule.getScene().clear();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            MainActivity.this,
                            "Вы выбрали квест " + quest.getTitle(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
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

//        try {
//            gameModule.setCurrentQuest(questModule.getQuests().get(0));
//            gameModule.saveCurrentState();
//            gameModule.loadQuestState(1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // the order of calls below is important
        try {
            setUpArFragment();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setUpGameModule();
        setUpQuestFragment();

        questsListFragment = new QuestsListFragment();
        questsListFragment.setQuestCardClickedListener(showQuestInfoCallback);
        questsListFragment.setStartQuestCallback(startQuestCallback);

        startService(new Intent(this, GeolocationService.class));

        checkAuthorization();
        selectFragment(questsListFragment, QuestsListFragment.TAG, false);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, GeolocationService.class));
        super.onDestroy();
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
        Journal<String> journal = new Journal<>();
        journal.addNow("First record");
        journal.addNow("Second record");
        journal.addNow("Third record");
//        gameModule.addCurrentJournal(journal);

//        gameModule.addCurrentInventory(new Slot(0, Player.INVENTORY, false));

//        Places places = new Places();
//        places.addPlace(new Place(0, "First place", "Description")); //STUB!!!
//        gameModule.addCurrentPlaces(places);
    }

    private void setUpArFragment() throws FileNotFoundException {
        journalFragment = new JournalFragment();

        itemsListFragment = new ItemsListFragment();
        itemsListFragment.setOnItemClickListener(chooseItemOnClickListener);

        arFragment = new ARFragment();
        arFragment.setToInventoryOnClickListener(getSelectFragmentListener(itemsListFragment));
        arFragment.setToJournalOnClickListener(getSelectFragmentListener(journalFragment));
    }

    @OnClick(R.id.auth_activity_btn)
    public void goToAuthActivity(View v) {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ar_fragment_btn)
    public void goARFragment(View v) {
        if (gameModule.getCurrentQuest() == null) {
            Toast.makeText(this, "Сначала выбери квест", Toast.LENGTH_SHORT).show();
            return;
        }
        Place currentPlace = gameModule.getCurrentQuest().getPlaceMap().values().iterator().next();
        gameModule.setCurrentPlace(currentPlace);
        selectFragment(arFragment, ARFragment.TAG);
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
        selectFragment(fragment, tag, true);
    }

    private void selectFragment(Fragment fragment, String tag, boolean needTransaction) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;
        if (fragment.isAdded()) {
            return;
        }

        boolean needPut = true;
        Fragment lastFragment;
        if (index >= 0) {
            FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(index);
            String lastTag = backEntry.getName();
            lastFragment = fragmentManager.findFragmentByTag(lastTag);

            needPut = lastFragment != fragment;
        }

        if (needPut) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, fragment, tag);

            if (needTransaction) {
                fragmentTransaction.addToBackStack(tag);
            }
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
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
