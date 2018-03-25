package com.google.ar.core.examples.java.helloar.auth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.ar.core.examples.java.helloar.PermissionHelper;
import com.google.ar.core.examples.java.helloar.R;

public class AuthActivity extends AppCompatActivity {
    private LoginFragment loginFragment;
    private RegistrationFragment registrationFragment;
    private Fragment activeFragment;

    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(registrationFragment);
        }
    };

    private View.OnClickListener registerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(loginFragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        registrationFragment = new RegistrationFragment();
        registrationFragment.setOnClickListener(registerOnClickListener);

        loginFragment = new LoginFragment();
        loginFragment.setOnClickListener(loginOnClickListener);

        if (!PermissionHelper.hasInternetPermission(this)) {
            PermissionHelper.requestInternetPermission(this);
        }

        selectFragment(loginFragment);
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
        fragmentTransaction.add(R.id.auth_fragment_container, activeFragment);
        fragmentTransaction.commit();
    }

}
