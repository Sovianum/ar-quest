package com.google.ar.core.examples.java.helloar.quest.quests;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.model.Quest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Quest> quests;
    private QuestsListFragment fragment;

    public interface OnItemClickListener {
        void onItemClick(Quest item);
    }

    private final OnItemClickListener onItemClickListener;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_txt)
        TextView titleView;
        @BindView(R.id.description_txt)
        TextView descriptionView;
        @BindView(R.id.expander_view)
        TextView expanderView;
        @BindView(R.id.ratingBar_quest)
        RatingBar ratingBar;

        int defaultMaxLines;

        CardViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            defaultMaxLines = descriptionView.getMaxLines();
        }

        public void bind(final Quest item, final OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(item);
                }
            });
        }
    }

    public QuestAdapter(QuestsListFragment fragment, List<Quest> quests, OnItemClickListener listener) {
        this.fragment = fragment;
        this.quests = quests;
        this.onItemClickListener = listener;
        notifyDataSetChanged();
    }

    public void serItems(List<Quest> quests) {
        this.quests = quests;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int CARD_ID = R.layout.item_quest_card;
        View view = LayoutInflater.from(context).inflate(CARD_ID, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Quest quest = quests.get(position);

        final CardViewHolder cardHolder = (CardViewHolder) holder;
        cardHolder.titleView.setText(quest.getTitle());
        cardHolder.descriptionView.setText(quest.getDescription());
        cardHolder.ratingBar.setRating(quest.getRating());

        cardHolder.expanderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardHolder.descriptionView.getMaxLines() == cardHolder.defaultMaxLines) {
                    cardHolder.descriptionView.setMaxLines(Integer.MAX_VALUE);
                    cardHolder.expanderView.setText(R.string.hide_str);
                } else {
                    cardHolder.descriptionView.setMaxLines(cardHolder.defaultMaxLines);
                    cardHolder.expanderView.setText(R.string.show_more_str);
                }
            }
        });

        cardHolder.bind(quest, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }
}
