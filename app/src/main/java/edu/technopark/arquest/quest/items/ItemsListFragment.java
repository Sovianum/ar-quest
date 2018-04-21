package edu.technopark.arquest.quest.items;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import edu.technopark.arquest.App;
import edu.technopark.arquest.GameModule;
import edu.technopark.arquest.R;
import edu.technopark.arquest.core.game.Item;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsListFragment extends Fragment {
    public static final String TAG = ItemsListFragment.class.getSimpleName();

    private ItemAdapter adapter;

    @BindView(R.id.itemsRecyclerView)
    RecyclerView recyclerView;
    private ItemAdapter.OnItemClickListener onItemClickListener;

    @Inject
    GameModule gameModule;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        App.getAppComponent().inject(this);

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_items_list, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new ItemAdapter(new ArrayList<Item>(), onItemClickListener);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
    }

    public void loadItems(List<Item> items) {
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    public void setOnItemClickListener(ItemAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private void refreshItems() {
        loadItems(gameModule.getCurrentInventory().getItems());
    }

}
