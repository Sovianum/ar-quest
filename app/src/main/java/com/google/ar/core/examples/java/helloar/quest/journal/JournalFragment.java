package com.google.ar.core.examples.java.helloar.quest.journal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.model.JournalMessage;

import java.util.ArrayList;
import java.util.List;

public class JournalFragment extends Fragment {
    private MessageAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_journal, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new MessageAdapter(new ArrayList<JournalMessage>());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
    }

    private void refreshItems() {
        List<JournalMessage> items = new ArrayList<>();
        items.add(new JournalMessage("first message"));
        items.add(new JournalMessage("second message"));
        items.add(new JournalMessage("third message"));
        loadItems(items);
    }

    private void loadItems(List<JournalMessage> items) {
        if (adapter != null) {
            adapter.setItems(items);
        }
    }
}
