package technopark.diploma.arquest.auth;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import technopark.diploma.arquest.MainActivity;
import technopark.diploma.arquest.R;

public class AuthFragment extends Fragment {

    protected void onGettingToken(String token) {
        if (getActivity() == null) {
            return;
        }

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).edit();

        editor.putString(getResources().getString(R.string.json_web_token), token);
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    //only for debug!!!
    protected void getStubsToken() {
        String tokenStub = "tokeStub";
        onGettingToken(tokenStub);
    }
 }
