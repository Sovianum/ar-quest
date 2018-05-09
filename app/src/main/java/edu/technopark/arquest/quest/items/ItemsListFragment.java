package edu.technopark.arquest.quest.items;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.technopark.arquest.App;
import edu.technopark.arquest.GameModule;
import edu.technopark.arquest.R;
import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.quest.EmptyRecyclerView;

public class ItemsListFragment extends Fragment {
    public static final String TAG = ItemsListFragment.class.getSimpleName();

    private ItemAdapter adapter;

    @BindView(R.id.itemsRecyclerView)
    EmptyRecyclerView recyclerView;
    private ItemAdapter.OnItemClickListener onItemClickListener;

    @BindView(R.id.empty_view_inventory)
    TextView emptyTextView;

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
        recyclerView.setEmptyView(emptyTextView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
    }

    public void refreshItems() {
        if (gameModule == null) {
            return;
        }
        if (gameModule.getCurrentInventory() == null) { //stubs for compatibility!!!
            loadItems(new ArrayList<Item>());
        } else {
            loadItems(gameModule.getCurrentInventory().getItems());
        }
    }

    public void loadItems(List<Item> items) {
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    public void setOnItemClickListener(ItemAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
