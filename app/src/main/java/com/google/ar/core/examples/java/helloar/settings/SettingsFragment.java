package com.google.ar.core.examples.java.helloar.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.ar.core.examples.java.helloar.App;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.network.NetworkModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {
    @Inject
    NetworkModule networkModule;

    @BindView(R.id.logout_btn)
    Button logoutButton;

    @BindView(R.id.stop_service_btn)
    Button stopServiceButton;

    public static final String TAG = SettingsFragment.class.getSimpleName();

    private View.OnClickListener onLogoutClickListener;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        if (onLogoutClickListener != null) {
            logoutButton.setOnClickListener(onLogoutClickListener);
        }
        return view;
    }

    public void setOnLogoutClickListener(View.OnClickListener listener) {
        onLogoutClickListener = listener;
        if (logoutButton != null) {
            logoutButton.setOnClickListener(onLogoutClickListener);
        }
    }

}
