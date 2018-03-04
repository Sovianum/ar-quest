package com.google.ar.core.examples.java.helloar.auth;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.R;

public class RegistrationFragment extends AuthFragment {
    private EditText loginText;
    private EditText passwordText;
    private Button registerButton;
    private ProgressBar progressBar;
    private TextView linkToLogin;
    private View.OnClickListener onClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_registration, container, false);
        loginText = view.findViewById(R.id.login_edit);
        passwordText = view.findViewById(R.id.password_edit);
        registerButton = view.findViewById(R.id.register_btn);
        progressBar = view.findViewById(R.id.progressBar);
        linkToLogin = view.findViewById(R.id.link_to_login);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoading(true);
                getStubsToken(); //debug
            }
        });

        linkToLogin.setOnClickListener(onClickListener);
        return view;
        }

    public void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

}
