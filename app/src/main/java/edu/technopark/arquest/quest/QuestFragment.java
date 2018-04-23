package edu.technopark.arquest.quest;


import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.technopark.arquest.App;
import edu.technopark.arquest.GameModule;
import edu.technopark.arquest.R;
import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.journal.Journal;
import edu.technopark.arquest.game.journal.TimestampRecord;
import edu.technopark.arquest.quest.items.ItemAdapter;
import edu.technopark.arquest.quest.place.PlacesAdapter;

public class QuestFragment extends Fragment {
    public static final String TAG = QuestFragment.class.getSimpleName();

    @BindView(R.id.to_ar_mode_btn)
    Button toARModeBtn;

    @BindView(R.id.cancel_quest_btn)
    Button cancelQuestBtn;

    @BindView(R.id.journal_txt)
    TextView journalText;

    @BindView(R.id.inventory_txt)
    TextView inventoryText;

    @BindView(R.id.places_txt)
    TextView placesText;

    @BindView(R.id.last_journal_record_card)
    View lastJournalCard;

    @BindView(R.id.message_text)
    TextView messageTextView;

    @BindView(R.id.message_time)
    TextView messageDateView;

    @BindView(R.id.itemsRecyclerView)
    RecyclerView recyclerViewItems;

    @BindView(R.id.placesRecyclerView)
    RecyclerView recyclerViewPlaces;

    private View.OnClickListener onARModeBtnClickListener;
    private View.OnClickListener onCancelBtnClickListener;
    private View.OnClickListener onJournalClickListener;
    private View.OnClickListener onPlacesClickListener;
    private View.OnClickListener onInventoryClickListener;

    private ItemAdapter itemAdapter;
    private PlacesAdapter placesAdapter;

    private ItemAdapter.OnItemClickListener onItemClickListener;

    @Inject
    GameModule gameModule;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        App.getAppComponent().inject(this);

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_quest, container, false);
        ButterKnife.bind(this, view);
        if (onARModeBtnClickListener != null) {
            toARModeBtn.setOnClickListener(onARModeBtnClickListener);
        }
        if (onCancelBtnClickListener != null) {
            cancelQuestBtn.setOnClickListener(onCancelBtnClickListener);
        }
        if (onJournalClickListener != null) {
            journalText.setOnClickListener(onJournalClickListener);
        }
        if (onPlacesClickListener != null) {
            placesText.setOnClickListener(onPlacesClickListener);
        }
        refreshLastJournalRecord();
        if (onInventoryClickListener != null) {
            inventoryText.setOnClickListener(onInventoryClickListener);
        }

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerViewItems.setLayoutManager(manager);
        itemAdapter = new ItemAdapter(new ArrayList<Item>(), onItemClickListener);
        recyclerViewItems.setAdapter(itemAdapter);

        LinearLayoutManager managerPlaces = new LinearLayoutManager(getActivity());
        recyclerViewPlaces.setLayoutManager(managerPlaces);
        placesAdapter = new PlacesAdapter();
        recyclerViewPlaces.setAdapter(placesAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setOnARModeBtnClickListener(View.OnClickListener listener) {
        onARModeBtnClickListener = listener;
        if (toARModeBtn != null) {
            toARModeBtn.setOnClickListener(listener);
        }
    }

    public void setOnCancelBtnClickListener(View.OnClickListener listener) {
        onCancelBtnClickListener = listener;
        if (cancelQuestBtn != null) {
            cancelQuestBtn.setOnClickListener(listener);
        }
    }

    public void setOnJournalClickListener(View.OnClickListener listener) {
        this.onJournalClickListener = listener;
        if (journalText != null) {
            journalText.setOnClickListener(onJournalClickListener);
        }
    }

    public void setOnPlacesClickListener(View.OnClickListener listener) {
        this.onPlacesClickListener = listener;
        if (placesText != null) {
            placesText.setOnClickListener(onPlacesClickListener);
        }
    }

    public void setOnInventoryClickListener(View.OnClickListener listener) {
        this.onInventoryClickListener = listener;
        if (inventoryText != null) {
            inventoryText.setOnClickListener(onInventoryClickListener);
        }
    }


    public void refreshLastJournalRecord() {
        final Journal<String> journal = gameModule.getCurrentJournal();
        try {
            TimestampRecord<String> lastMessage = journal.getRecords().get(journal.getRecords().size() - 1);
            messageTextView.setText(lastMessage.getData());
            messageDateView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    lastMessage.getTime()));
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
        refreshPlaces();
    }

    private void refreshItems() {
        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
        }
    }

    private void refreshPlaces() {
        if (placesAdapter != null) {
            placesAdapter.notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(ItemAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
