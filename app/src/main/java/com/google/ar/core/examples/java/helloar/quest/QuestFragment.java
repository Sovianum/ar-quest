package com.google.ar.core.examples.java.helloar.quest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.R;

public class QuestFragment extends Fragment {
    private View.OnClickListener onARModeBtnClickListener;
    private View.OnClickListener onJournalClickListener;
    private Button toARModeBtn;
    private TextView journalText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_quest, container, false);
        toARModeBtn = view.findViewById(R.id.to_ar_mode_btn);
        toARModeBtn.setOnClickListener(onARModeBtnClickListener);

        journalText = view.findViewById(R.id.journal_txt);
        journalText.setOnClickListener(onJournalClickListener);
        return view;
    }

    public void setOnARModeBtnClickListener(View.OnClickListener listener) {
        this.onARModeBtnClickListener = listener;
    }

    public void setOnJournalClickListener(View.OnClickListener listener) {
        this.onJournalClickListener = listener;
    }


}
