package com.google.ar.core.examples.java.helloar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.ar.core.examples.java.helloar.auth.AuthActivity;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.model.Quest;
import com.google.ar.core.examples.java.helloar.network.NetworkModule;
import com.google.ar.core.examples.java.helloar.quest.ARFragment;
import com.google.ar.core.examples.java.helloar.quest.QuestFragment;
import com.google.ar.core.examples.java.helloar.quest.game.QuestModule;
import com.google.ar.core.examples.java.helloar.quest.items.ItemAdapter;
import com.google.ar.core.examples.java.helloar.quest.items.ItemsListFragment;
import com.google.ar.core.examples.java.helloar.quest.journal.JournalFragment;
import com.google.ar.core.examples.java.helloar.quest.place.PlaceFragment;
import com.google.ar.core.examples.java.helloar.quest.quests.QuestsListFragment;
import com.google.ar.core.examples.java.helloar.settings.SettingsFragment;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private QuestsListFragment questsListFragment;
    private ARFragment arFragment;
    private QuestFragment questFragment;
    private ItemsListFragment itemsListFragment;
    private JournalFragment journalFragment;
    private PlaceFragment placeFragment;
    private SettingsFragment settingsFragment;

    @Inject
    GameModule gameModule;

    @Inject
    QuestModule questModule;

    @Inject
    NetworkModule networkModule;

    @Inject
    HintModule hintModule;

    private QuestsListFragment.OnQuestReactor showQuestInfoCallback = new QuestsListFragment.OnQuestReactor() {
        @Override
        public void onQuestReact(Quest quest) {
            goToCurrentQuest();
        }
    };

    private QuestsListFragment.OnQuestReactor startQuestCallback = new QuestsListFragment.OnQuestReactor() {
        @Override
        public void onQuestReact(final Quest quest) {
            gameModule.setCurrentQuest(quest);
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

    private View.OnClickListener onCloseARClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private View.OnClickListener onLogoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            removeToken();
            checkAuthorization();
        }
    };

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

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

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_quests:
                        selectFragment(questsListFragment, QuestsListFragment.TAG, false);
                        break;
                    case R.id.action_current_quest:
                        goToCurrentQuest();
                        break;
                    case R.id.action_ar:
                        goARFragment();
                        break;

                    case R.id.action_settings:
                        selectFragment(settingsFragment, SettingsFragment.TAG, false);
                        break;
                }
                return false;
            }
        });

        settingsFragment = new SettingsFragment();
        settingsFragment.setOnLogoutClickListener(onLogoutClickListener);


        startService(new Intent(this, GeolocationService.class));

        //checkAuthorization(); //commented for focus group testing
        selectFragment(questsListFragment, QuestsListFragment.TAG, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showGreeting();
        hintModule.setActivity(this);
        selectFragmentByView(questsListFragment, QuestsListFragment.TAG);

        hintModule.addHint(R.id.start_ar_hint, new HintModule.NoCompleteHint() {
            @Override
            public void setUpHint(ShowcaseView sv) {
                sv.setTarget(new ViewTarget(
                        bottomNavigationView.findViewById(R.id.action_ar)
                ));
                sv.setContentText(getString(R.string.start_ar_str));
            }
        });
    }

    @Override
    protected void onDestroy() {
        System.out.println("Destroy activity");
        //stopService(new Intent(this, GeolocationService.class));
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

    @Override
    public void onBackPressed() {
        //if (getSupportFragmentManager().getBackStackEntryCount() > 0 ) {
        //    getSupportFragmentManager().popBackStackImmediate();
        //}
        showBottomNavigation();
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionHelper.hasPermissions(this)) {
            //Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
            //        .show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.non_permission_message)
                    .setTitle(R.string.non_permission_title)
                    .setCancelable(false)
                    .setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    checkPermission();
                                }
                            });

            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void showGreeting() {
        if (isFirstLaunch()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.greeting_message)
                    .setTitle(R.string.greeting_title)
                    .setCancelable(true)
                    .setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    checkPermission();
                                }
                            });

            alertDialog = builder.create();
            alertDialog.show();
            setFirstLaunch(false);
        }
    }

    private void checkPermission() {
        if (PermissionHelper.hasPermissions(this)) {

        } else {
            PermissionHelper.requestPermissions(this);
        }
    }

    private boolean isFirstLaunch() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(getString(R.string.first_launch), true);
    }

    private void setFirstLaunch(boolean isFirstLauch) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).edit();

        editor.putBoolean(getString(R.string.first_launch), isFirstLauch);
        editor.apply();
    }

    private void setUpQuestFragment() {
        placeFragment = new PlaceFragment();

        questFragment = new QuestFragment();
        questFragment.setOnARModeBtnClickListener(getSelectFragmentListener(arFragment));
        questFragment.setOnJournalClickListener(getSelectFragmentListener(journalFragment));
        questFragment.setOnItemClickListener(chooseItemOnClickListener);
        questFragment.setOnPlacesClickListener(getSelectFragmentListener(placeFragment));
        questFragment.setOnInventoryClickListener(getSelectFragmentListener(itemsListFragment));
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

        Place place = questModule.getNewStyleInteractionDemoPlaceFromScript();

        arFragment = new ARFragment();
        arFragment.setToInventoryOnClickListener(getSelectFragmentListener(itemsListFragment));
        arFragment.setToJournalOnClickListener(getSelectFragmentListener(journalFragment));

        arFragment.setDecorations(place);

        arFragment.setCloseOnClickListener(onCloseARClickListener);
    }

    public void goToAuthActivity(View v) {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    public void goARFragment() {
        if (gameModule.getCurrentQuest() == null) {
            Toast.makeText(this, "Сначала выбери квест", Toast.LENGTH_SHORT).show();
            return;
        }
        Place currentPlace = gameModule.getCurrentQuest().getPlaceMap().values().iterator().next();
        gameModule.setCurrentPlace(currentPlace);
        selectFragment(arFragment, ARFragment.TAG);
    }

    public void goToCurrentQuest() {
        if (gameModule.getCurrentQuest() == null) {
            Toast.makeText(this, "Сначала выбери квест", Toast.LENGTH_SHORT).show();
            return;
        }
        Place currentPlace = gameModule.getCurrentQuest().getPlaceMap().values().iterator().next();
        gameModule.setCurrentPlace(currentPlace);
        selectFragment(questFragment, QuestFragment.TAG);
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

    private void selectFragmentByView(Fragment fragment, String tag) {
        selectFragment(fragment, tag, false);
    }

    private void selectFragment(Fragment fragment, String tag, boolean fromNav) {
        showOrHideBottomNavigation(tag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;

        boolean needAdd = true;
        if (index >= 0) {
            if (isFragmentInBackstack(fragmentManager,tag)) {
                fragmentManager.popBackStackImmediate(tag, 0);
                needAdd = false;
            }
        }

        //if (fromNav) {
        //    for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
        //        fragmentManager.popBackStack();
        //    }
        //}

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment, tag);

        if (needAdd) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    private void showOrHideBottomNavigation(String tag) {
        if (tag.equals(ARFragment.TAG)) {
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    private void showBottomNavigation() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private static boolean isFragmentInBackstack(final FragmentManager fragmentManager, final String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
            if (fragmentTagName.equals(fragmentManager.getBackStackEntryAt(entry).getName())) {
                return true;
            }
        }
        return false;
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
