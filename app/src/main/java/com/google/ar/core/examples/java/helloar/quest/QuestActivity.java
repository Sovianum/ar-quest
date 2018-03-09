package com.google.ar.core.examples.java.helloar.quest;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.ar.core.examples.java.helloar.R;

public class QuestActivity extends AppCompatActivity {
    private ARFragment arFragment;
    private QuestFragment questFragment;
    private Fragment activeFragment;

    private View.OnClickListener toQuestOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(questFragment);
        }
    };

    private View.OnClickListener toAROnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(arFragment);
        }
    };

    private LocationListener onSelfLocationChangeListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Log.e("INFO", String.valueOf(location.getLatitude()) + " " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        questFragment = new QuestFragment();
        questFragment.setOnClickListener(toAROnClickListener);

        arFragment = new ARFragment();
        arFragment.setOnClickListener(toQuestOnClickListener);

        selectFragment(arFragment);
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
        fragmentTransaction.add(R.id.quest_fragment_container, activeFragment);
        fragmentTransaction.commit();
    }
}
