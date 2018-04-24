package edu.technopark.arquest.quest.place;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.technopark.arquest.App;
import edu.technopark.arquest.R;
import edu.technopark.arquest.quest.EmptyRecyclerView;

public class PlaceFragment extends Fragment {
    public static final String TAG = PlaceFragment.class.getSimpleName();

    private PlacesAdapter adapter;

    @BindView(R.id.placesRecyclerView)
    EmptyRecyclerView recyclerView;

    @BindView(R.id.empty_view_places)
    TextView emptyTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_checkpoints, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new PlacesAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(emptyTextView);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        refreshItems();
    }

    private void refreshItems() { //stubs
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
