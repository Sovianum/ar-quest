package com.google.ar.core.examples.java.helloar.quest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.ar.core.examples.java.helloar.R;

public class QuestFragment extends Fragment {
    private View.OnClickListener onClickListener;
    private Button toARModeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_quest, container, false);
        toARModeBtn = view.findViewById(R.id.to_ar_mode_btn);
        toARModeBtn.setOnClickListener(onClickListener);
        return view;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }


}
