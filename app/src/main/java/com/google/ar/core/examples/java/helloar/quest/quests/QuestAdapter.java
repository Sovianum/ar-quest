package com.google.ar.core.examples.java.helloar.quest.quests;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.ar.core.examples.java.helloar.App;
import com.google.ar.core.examples.java.helloar.HintModule;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.model.Quest;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static QuestAdapter.OnItemClickListener itemClickListenerFromReactor(final QuestsListFragment.OnQuestReactor reactor) {
        return new QuestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Quest item) {
                reactor.onQuestReact(item);
            }
        };
    }

    private List<Quest> quests;
    private QuestsListFragment fragment;

    public interface OnItemClickListener {
        void onItemClick(Quest item);
    }

    private final QuestsListFragment.OnQuestReactor onItemClickListener;
    private final QuestsListFragment.OnQuestReactor startQuestClickListener;

    @Inject
    HintModule hintModule;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_txt)
        TextView titleView;
        @BindView(R.id.description_txt)
        TextView descriptionView;
        @BindView(R.id.expander_view)
        TextView expanderView;
        //@BindView(R.id.start_quest_view)
        //TextView startQuestTextView;
        @BindView(R.id.start_quest_btn)
        Button startQuestButton;
        @BindView(R.id.ratingBar_quest)
        RatingBar ratingBar;

        @Inject
        HintModule hintModule;

        @Inject
        Context context;

        int defaultMaxLines;

        CardViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            defaultMaxLines = descriptionView.getMaxLines();
            App.getAppComponent().inject(this);

        }

        public void bind(final Quest item, final OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(item);
                }
            });

            hintModule.addHint(R.id.select_quest_hint_name, new HintModule.NoCompleteHint() {
                @Override
                public void setUpHint(ShowcaseView sv) {
                    sv.setContentText(context.getString(R.string.select_quest_hint_str));
                    //Target target = new ViewTarget(startQuestTextView);
                    Target target = new ViewTarget(startQuestButton);
                    sv.setTarget(target);
                }
            });
        }
    }

    public QuestAdapter(
            QuestsListFragment fragment, List<Quest> quests,
            QuestsListFragment.OnQuestReactor onItemClickListener,
            QuestsListFragment.OnQuestReactor startQuestClickListener
    ) {
        App.getAppComponent().inject(this);
        this.fragment = fragment;
        this.quests = quests;
        this.onItemClickListener = onItemClickListener;
        this.startQuestClickListener = startQuestClickListener;
        hintModule.requestHint(R.id.select_quest_hint_name);
        notifyDataSetChanged();
    }

    public void setItems(List<Quest> quests) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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

        if(cardHolder.expanderView.getLineCount() < cardHolder.defaultMaxLines) {
            cardHolder.expanderView.setVisibility(View.GONE);
        }
        //cardHolder.startQuestTextView.setOnClickListener(new View.OnClickListener() {
        cardHolder.startQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuestClickListener.onQuestReact(quest);
                hintModule.showHintOnce(R.id.start_ar_hint);
            }
        });

        cardHolder.bind(quest, itemClickListenerFromReactor(onItemClickListener));
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }
}
