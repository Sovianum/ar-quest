package edu.technopark.arquest.quest.items;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.technopark.arquest.App;
import edu.technopark.arquest.GameModule;
import edu.technopark.arquest.R;
import edu.technopark.arquest.game.Item;

public class ItemsListView extends LinearLayout {
    public static final String TAG = ItemsListView.class.getSimpleName();

    private ItemAdapter adapter;
    private ItemAdapter.OnItemClickListener onItemClickListener;

    @Inject
    GameModule gameModule;

    @BindView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @BindView(R.id.itemsRecyclerView)
    RecyclerView recyclerView;

    public ItemsListView(Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_items_list, this);
        ButterKnife.bind(this, view);
        toolbar.setTitle(";flasjf;aslkjf");
    }

    public void setUpView(LinearLayoutManager manager) {
        App.getAppComponent().inject(this);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(manager);
        adapter = new ItemAdapter(new ArrayList<Item>(), onItemClickListener);
        recyclerView.setAdapter(adapter);

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
        if (gameModule.getCurrentInventory() == null) { //stubs for compatibility!!!
            loadItems(new ArrayList<Item>());
        } else {
            loadItems(gameModule.getCurrentInventory().getItems());
        }
    }

}
