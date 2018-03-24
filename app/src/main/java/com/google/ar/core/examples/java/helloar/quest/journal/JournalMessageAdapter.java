package com.google.ar.core.examples.java.helloar.quest.journal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.App;
import com.google.ar.core.examples.java.helloar.GameModule;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.core.game.journal.TimestampRecord;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    GameModule gameModule;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_text)
        TextView messageTextView;
        @BindView(R.id.message_time)
        TextView messageDateView;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public JournalMessageAdapter() {
        App.getAppComponent().inject(this);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int CARD_ID = R.layout.item_message_card;
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new JournalMessageAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TimestampRecord<String> message = gameModule.getCurrentJournal().getRecords().get(position);
        final CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.messageTextView.setText(message.getData());
        cardHolder.messageDateView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                message.getTime()));
    }

    @Override
    public int getItemCount() {
        return gameModule.getCurrentJournal().getRecords().size();
    }
}
