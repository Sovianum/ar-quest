package com.google.ar.core.examples.java.helloar.auth;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends AuthFragment {
    @BindView(R.id.login_edit)
    EditText loginText;

    @BindView(R.id.password_edit)
    EditText passwordText;

    @BindView(R.id.login_btn)
    Button loginButton;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.link_to_register)
    TextView linkToRegister;

    private View.OnClickListener onClickListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        if (onClickListener != null) {
            linkToRegister.setOnClickListener(onClickListener);
        }
        return view;
    }

    @OnClick(R.id.login_btn)
    public void onLoginButtonClickListener() {
        progressBar.setVisibility(View.VISIBLE);
        getStubsToken(); //debug
    }

    // TODO remove or use
    public void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        onClickListener = listener;
        if (linkToRegister != null) {
            linkToRegister.setOnClickListener(onClickListener);
        }
    }
}
