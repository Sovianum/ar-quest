package edu.technopark.arquest.settings;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.technopark.arquest.App;
import edu.technopark.arquest.GeolocationService;
import edu.technopark.arquest.R;
import edu.technopark.arquest.network.NetworkModule;

public class SettingsFragment extends Fragment {
    @Inject
    NetworkModule networkModule;

    //@BindView(R.id.logout_btn)
    //Button logoutButton;

    @BindView(R.id.switch_geoservice_btn)
    Switch switchGeo;

    @BindView(R.id.switch_geoservice_text)
    TextView switchText;

    public static final String TAG = SettingsFragment.class.getSimpleName();

    private View.OnClickListener onLogoutClickListener;

    private final String switchOn = "отслеживание включено";
    private final String switchOff = "отслеживание выключено";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        //if (onLogoutClickListener != null) {
        //    logoutButton.setOnClickListener(onLogoutClickListener);
        //}
        setForeground();
        switchGeo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    getActivity().startService(new Intent(getActivity(), GeolocationService.class)
                            .putExtra(getString(R.string.foreground), true));
                    setForegroundTracking(true);
                    switchText.setText(switchOn);
                } else {
                    confirmGeoSwitchOffAlert();
                }
            }
        });
        return view;
    }

    private void setForeground() {
        if (isForegroundTracking()) {
            switchGeo.setChecked(true);
            switchText.setText(switchOn);
        } else {
            switchGeo.setChecked(false);
            switchText.setText(switchOff);
        }
    }

    private void confirmGeoSwitchOffAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.switch_geo_message)
                .setTitle(R.string.switch_geo_title)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                getActivity().startService(new Intent(getActivity(), GeolocationService.class)
                                        .putExtra(getString(R.string.foreground), false));
                                setForegroundTracking(false);
                                switchText.setText(switchOff);
                            }
                        });
        builder.setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        switchGeo.setChecked(true);
                    }
                });

        builder.create().show();
    }

    private boolean isForegroundTracking() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        return prefs.getBoolean(getString(R.string.foreground_tracking), true);
    }

    private void setForegroundTracking(boolean isForeground) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();

        editor.putBoolean(getString(R.string.foreground_tracking), isForeground);
        editor.apply();
    }

    //public void setOnLogoutClickListener(View.OnClickListener listener) {
    //    onLogoutClickListener = listener;
    //    if (logoutButton != null) {
    //        logoutButton.setOnClickListener(onLogoutClickListener);
    //    }
    //}

}
