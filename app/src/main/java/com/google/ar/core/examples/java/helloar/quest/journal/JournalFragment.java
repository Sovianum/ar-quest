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
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.network.Api;

public class JournalFragment extends Fragment {
    public static final String TAG = JournalFragment.class.getSimpleName();

    private JournalMessageAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_journal, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new JournalMessageAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
    }

    private void refreshItems() { //stubs
        loadItems(Api.getJournals().getCurrentJournal());
    }

    private void loadItems(Journal journal) {
        if (adapter != null) {
            adapter.setItems(journal);
        }
    }
}
