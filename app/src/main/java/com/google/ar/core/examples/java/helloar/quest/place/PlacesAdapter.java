package com.google.ar.core.examples.java.helloar.quest.place;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.GameApi;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.core.game.Place;

public class PlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Places items;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView descriptionView;

        public CardViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title_txt);
            descriptionView = itemView.findViewById(R.id.description_txt);
        }
    }

    public PlacesAdapter() {
        this.items = GameApi.getPlacesStorage().getCurrentPlaces();
        notifyDataSetChanged();
    }


    public void setItems(Places items) {
        this.items = items;
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
        final Place item = items.getCheckpoints().get(position);

        final CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.titleView.setText(item.getName());
        cardHolder.descriptionView.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return items.getCheckpoints().size();
    }
}
