package com.google.ar.core.examples.java.helloar.quest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.core.game.journal.TimestampRecord;
import com.google.ar.core.examples.java.helloar.model.Item;
import com.google.ar.core.examples.java.helloar.network.Api;
import com.google.ar.core.examples.java.helloar.quest.items.ItemAdapter;
import com.google.ar.core.examples.java.helloar.quest.place.Checkpoint;
import com.google.ar.core.examples.java.helloar.quest.place.CheckpointsAdapter;

import java.util.ArrayList;
import java.util.List;

public class QuestFragment extends Fragment {
    public static final String TAG = QuestFragment.class.getSimpleName();

    private View.OnClickListener onARModeBtnClickListener;
    private View.OnClickListener onJournalClickListener;
    private Button toARModeBtn;
    private TextView journalText;
    private View lastJournalCard;
    TextView messageTextView;
    TextView messageDateView;
    TextView checkpointTitleView;
    TextView checkpointDescriptionView;
    private Journal<String> journal;

    private ItemAdapter itemAdapter;
    private ItemAdapter.OnItemClickListener onItemClickListener;
    private RecyclerView recyclerViewItems;
    private RecyclerView recyclerViewCheckpoints;

    private CheckpointsAdapter checkpointsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setJournal();
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_quest, container, false);
        toARModeBtn = view.findViewById(R.id.to_ar_mode_btn);
        toARModeBtn.setOnClickListener(onARModeBtnClickListener);

        journalText = view.findViewById(R.id.journal_txt);
        journalText.setOnClickListener(onJournalClickListener);

        lastJournalCard = view.findViewById(R.id.last_journal_record_card);
        messageTextView = lastJournalCard.findViewById(R.id.message_text);
        messageDateView = lastJournalCard.findViewById(R.id.message_time);
        refreshLastJournalRecord();

        recyclerViewItems = view.findViewById(R.id.items_list_layout).findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerViewItems.setLayoutManager(manager);
        itemAdapter = new ItemAdapter(new ArrayList<Item>(), onItemClickListener);
        recyclerViewItems.setAdapter(itemAdapter);

        recyclerViewCheckpoints = view.findViewById(R.id.checkpoints_card).findViewById(R.id.recyclerView);
        LinearLayoutManager managerCheckpoints = new LinearLayoutManager(getActivity());
        recyclerViewCheckpoints.setLayoutManager(managerCheckpoints);
        checkpointsAdapter = new CheckpointsAdapter(new ArrayList<Checkpoint>());
        recyclerViewCheckpoints.setAdapter(checkpointsAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setOnARModeBtnClickListener(View.OnClickListener listener) {
        this.onARModeBtnClickListener = listener;
    }

    public void setOnJournalClickListener(View.OnClickListener listener) {
        this.onJournalClickListener = listener;
    }

    public void setJournal() {
        this.journal = Api.getJournals().getCurrentJournal();
    }

    public void refreshLastJournalRecord() {
        TimestampRecord<String> lastMessage =  journal.getRecords().get(journal.getRecords().size() - 1);
        messageTextView.setText(lastMessage.getData());
        messageDateView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                lastMessage.getTime()));
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
        refreshCheckpoints();
    }

    private void refreshItems() {
        loadItems(Api.getInventories().getCurrentInventory().getItems());
    }

    private void refreshCheckpoints() {
        loadCheckpoints(Api.getCheckpointsStorage().getCurrentCheckpoints().getCheckpoints());
    }

    private void loadItems(List<Item> items) {
        if (itemAdapter != null) {
            itemAdapter.setItems(items);
        }
    }

    private void loadCheckpoints(List<Checkpoint> checkpoints) {
        if (checkpointsAdapter != null) {
            checkpointsAdapter.setItems(checkpoints);
        }
    }

    public void setOnItemClickListener(ItemAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
