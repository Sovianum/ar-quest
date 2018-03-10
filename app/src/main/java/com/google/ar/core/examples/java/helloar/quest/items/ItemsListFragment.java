package com.google.ar.core.examples.java.helloar.quest.items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemsListFragment extends Fragment {
    private ItemAdapter adapter;
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_items_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new ItemAdapter(new ArrayList<Item>());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
    }

    private void refreshItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("Меч", "Большой и страшный меч", ""));
        items.add(new Item("Щит", "Маленький и забавный щит", ""));
        loadItems(items);
    }

    private void loadItems(List<Item> items) {
        if (adapter != null) {
            adapter.setItems(items);
        }
    }
}
