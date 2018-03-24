package com.google.ar.core.examples.java.helloar.quest.place;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.ar.core.examples.java.helloar.GameApi;
import com.google.ar.core.examples.java.helloar.R;

public class PlaceFragment extends Fragment {
    public static final String TAG = PlaceFragment.class.getSimpleName();

    private PlacesAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_checkpoints, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new PlacesAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
    }

    private void refreshItems() { //stubs
        loadItems(GameApi.getPlacesStorage().getCurrentPlaces());
    }

    private void loadItems(Places places) {
        if (adapter != null) {
            adapter.setItems(places);
        }
    }

}
