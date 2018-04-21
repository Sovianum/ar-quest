package edu.technopark.arquest.auth;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.technopark.arquest.App;
import edu.technopark.arquest.ListenerHandler;
import edu.technopark.arquest.R;
import edu.technopark.arquest.model.User;
import edu.technopark.arquest.network.NetworkError;
import edu.technopark.arquest.network.NetworkModule;
import edu.technopark.arquest.network.ServerResponse;

import java.net.UnknownHostException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class RegistrationFragment extends AuthFragment {
    @Inject
    NetworkModule networkModule;

    @BindView(R.id.login_edit)
    EditText loginText;

    @BindView(R.id.password_edit)
    EditText passwordText;

    @BindView(R.id.register_btn)
    Button registerButton;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.link_to_login)
    TextView linkToLogin;

    private View.OnClickListener onClickListener;

    private ListenerHandler<NetworkModule.OnDataGetListener<ServerResponse<String>>> handler;


    private NetworkModule.OnDataGetListener<ServerResponse<String>> onDataGetListener = new NetworkModule.OnDataGetListener<ServerResponse<String>>() {
        @Override
        public void onSuccess(ServerResponse<String> response) {
            if (response.getData() != null) {
                onGettingToken(response.getData());
                setLoading(false);
            }
        }

        @Override
        public void onError(Exception error) {
            Log.e(AuthActivity.class.getName(), error.toString());
            if (getActivity() == null) {
                return;
            }

            if (error instanceof NetworkError) {
                NetworkError casted = (NetworkError) error;
                int code = casted.getResponseCode();
                switch (code) {
                    case HTTP_NOT_FOUND: {
                        Toast.makeText(getActivity(), R.string.failed_to_authorize_str, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case HTTP_CONFLICT: {
                        Toast.makeText(getActivity(), R.string.duplicate_user_str, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case HTTP_INTERNAL_ERROR: {
                        Toast.makeText(getActivity(), R.string.server_internal_error_str, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default: {
                        Toast.makeText(getActivity(), ((NetworkError) error).getErrMsg(), Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            } else if (error instanceof UnknownHostException){
                Toast.makeText(getContext(), R.string.connection_lost_str, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
            setLoading(false);
        }
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, view);
        if (onClickListener != null) {
            linkToLogin.setOnClickListener(onClickListener);
        }
        return view;
    }

    @OnClick(R.id.register_btn)
    public void onRegisterButtonClick() {
        setLoading(true);
        final User user = new User();
        user.setLogin(loginText.getText().toString());
        user.setPassword(passwordText.getText().toString());
        setLoading(true);
        handler = networkModule.registerUser(onDataGetListener, user);
    }

    public void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        onClickListener = listener;
        if (linkToLogin != null) {
            linkToLogin.setOnClickListener(listener);
        }
    }
}
