package com.google.ar.core.examples.java.helloar.quest.place;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.App;
import com.google.ar.core.examples.java.helloar.GameModule;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.core.game.Place;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    GameModule gameModule;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_txt)
        TextView titleView;
        @BindView(R.id.description_txt)
        TextView descriptionView;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public PlacesAdapter() {
        App.getAppComponent().inject(this);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int CARD_ID = R.layout.item_checkpoint_card;
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new PlacesAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Place place = gameModule.getCurrentQuest().getAvailablePlaces().get(position);

        final CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.titleView.setText(place.getName());
        cardHolder.descriptionView.setText(place.getDescription());
    }

    @Override
    public int getItemCount() {
        return gameModule.getCurrentQuest().getAvailablePlaces().size();
    }
}
