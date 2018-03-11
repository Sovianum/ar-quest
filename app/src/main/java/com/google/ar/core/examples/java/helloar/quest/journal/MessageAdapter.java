package com.google.ar.core.examples.java.helloar.quest.journal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.model.JournalMessage;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messageDateView;

        public CardViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
            messageDateView = itemView.findViewById(R.id.message_time);
        }
    }

    private List<JournalMessage> items;

    public MessageAdapter(List<JournalMessage> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItems(List<JournalMessage> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int CARD_ID = R.layout.item_message_card;
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new MessageAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final JournalMessage message = items.get(position);

        final CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.messageTextView.setText(message.getMessageText());
        cardHolder.messageDateView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                message.getMessageTime()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
