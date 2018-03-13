package com.google.ar.core.examples.java.helloar.quest.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.core.game.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> items;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final ItemAdapter.OnItemClickListener onItemClickListener;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView descriptionView;
        ImageView imageView;

        public CardViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_img);
            nameView = itemView.findViewById(R.id.name_txt);
            descriptionView = itemView.findViewById(R.id.description_txt);
        }

        public void bind(final Item item, final OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(item);
                }
            });
        }
    }

    public ItemAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.onItemClickListener = listener;
        notifyDataSetChanged();
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int CARD_ID = R.layout.item_card;
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new ItemAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Item item = items.get(position);

        final CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.nameView.setText(item.getName());
        cardHolder.descriptionView.setText(item.getDescription());
        // todo add image loading
        cardHolder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
