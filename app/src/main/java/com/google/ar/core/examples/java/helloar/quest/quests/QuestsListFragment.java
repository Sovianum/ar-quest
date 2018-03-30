package com.google.ar.core.examples.java.helloar.quest.quests;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.ar.core.examples.java.helloar.App;
import com.google.ar.core.examples.java.helloar.HintModule;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.model.Quest;
import com.google.ar.core.examples.java.helloar.quest.game.QuestModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestsListFragment extends Fragment {
    public interface OnQuestReactor {
        void onQuestReact(Quest quest);
    }

    public static final String TAG = QuestsListFragment.class.getSimpleName();

    @BindView(R.id.questsRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private QuestAdapter adapter;
    private OnQuestReactor questCardClickedListener;
    private OnQuestReactor startQuestListener;

    @Inject
    QuestModule questModule;

    @Inject
    HintModule hintModule;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_quests_list, container, false);
        ButterKnife.bind(this, view);
        App.getAppComponent().inject(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new QuestAdapter(
                this, new ArrayList<Quest>(),
                questCardClickedListener,
                startQuestListener
        );
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setRefreshing(true);
        refreshItems();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void refreshItems() {
        loadItems(questModule.getQuests());
        setRefreshing(false);
    }

    public void loadItems(List<Quest> quests) {
        if (adapter != null) {
            adapter.setItems(quests);
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    public void setQuestCardClickedListener(OnQuestReactor listener) {
        this.questCardClickedListener = listener;
    }

    public void setStartQuestCallback(OnQuestReactor listener) {
        this.startQuestListener = listener;
    }
}
