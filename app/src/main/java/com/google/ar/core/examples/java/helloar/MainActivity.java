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
import android.widget.Button;

import com.google.ar.core.examples.java.helloar.auth.AuthActivity;
import com.google.ar.core.examples.java.helloar.network.Api;
import com.google.ar.core.examples.java.helloar.quest.QuestActivity;
import com.google.ar.core.examples.java.helloar.quest.list.QuestsListFragment;

public class MainActivity extends AppCompatActivity {
    private Button toQuestActivityButton;
    private Button toAuthActivityButton;

    private QuestsListFragment questsListFragment;

    private Fragment activeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //removeToken(); //debug

        toQuestActivityButton = findViewById(R.id.ar_activity_btn);
        toAuthActivityButton = findViewById(R.id.auth_activity_btn);
        toQuestActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToQuestActivity(view);
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
        questsListFragment = new QuestsListFragment();
        super.onStart();
        checkAuthorization();
        selectFragment(questsListFragment);
    }

    private void goToQuestActivity(View v) {
        Intent intent = new Intent(this, QuestActivity.class);
        startActivity(intent);
    }

    private void goToAuthActivity(View v) {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        return super.onCreateView(parent, name, context, attrs);
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
