package com.google.ar.core.examples.java.helloar.quest.place;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.R;

import java.util.List;

public class CheckpointsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Checkpoint> items;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView descriptionView;

        public CardViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title_txt);
            descriptionView = itemView.findViewById(R.id.description_txt);
        }
    }

    public CheckpointsAdapter(List<Checkpoint> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItems(List<Checkpoint> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int CARD_ID = R.layout.item_checkpoint_card;
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new CheckpointsAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Checkpoint item = items.get(position);

        final CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.titleView.setText(item.getTitle());
        cardHolder.descriptionView.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
