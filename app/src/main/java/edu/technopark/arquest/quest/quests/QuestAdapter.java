package edu.technopark.arquest.quest.quests;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.technopark.arquest.App;
import edu.technopark.arquest.HintModule;
import edu.technopark.arquest.R;
import edu.technopark.arquest.model.Quest;

import static edu.technopark.arquest.network.DownloadService.fileNameStubs;

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

    private Map<Integer, Integer> questItemMap = new HashMap<>();

    public interface OnItemClickListener {
        void onItemClick(Quest item);
    }

    private final QuestsListFragment.OnQuestReactor onItemClickListener;
    private final QuestsListFragment.OnQuestReactor startQuestClickListener;
    private int countLinesChar = 40;

    @Inject
    HintModule hintModule;

    @Inject
    Context context;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_txt)
        TextView titleView;
        @BindView(R.id.description_txt)
        TextView descriptionView;
        @BindView(R.id.expander_view)
        TextView expanderView;
        //@BindView(R.id.start_quest_view)
        //TextView startQuestTextView;
        @BindView(R.id.start_or_download_quest_btn)
        Button startQuestButton;
        //@BindView(R.id.ratingBar_quest)
        //RatingBar ratingBar;
        @BindView(R.id.progressDownload)
        ProgressBar mProgressBar;


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
                    //onItemClickListener.onItemClick(item);
                    startQuestClickListener.onQuestReact(item);
                }
            });
        }

        public void setDownloadable() {
            mProgressBar.setVisibility(View.GONE);
            /*startQuestButton.setText(fragment.getResources().getString(R.string.download_btn_str));
            startQuestButton.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(fragment.getActivity(),
                            R.drawable.ic_file_download_black_24dp), null);*/
        }

        public void setStartable() {
            mProgressBar.setVisibility(View.GONE);
            //startQuestButton.setText(fragment.getResources().getString(R.string.start_quest_str));
            //startQuestButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        public void setDownloadProgress(int downloadProgress) {
            mProgressBar.setProgress(downloadProgress);
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
        //cardHolder.ratingBar.setRating(quest.getRating());

        cardHolder.expanderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuestClickListener.onQuestReact(quest);
                //if (cardHolder.descriptionView.getMaxLines() == cardHolder.defaultMaxLines) {
                //    cardHolder.descriptionView.setMaxLines(Integer.MAX_VALUE);
                //    cardHolder.expanderView.setText(R.string.hide_str);
                //} else {
                //    cardHolder.descriptionView.setMaxLines(cardHolder.defaultMaxLines);
                //    cardHolder.expanderView.setText(R.string.show_more_str);
                //}
            }
        });

        if(cardHolder.descriptionView.length() < cardHolder.defaultMaxLines * countLinesChar) {
            cardHolder.expanderView.setVisibility(View.GONE);
        }
        //cardHolder.startQuestTextView.setOnClickListener(new View.OnClickListener() {


        if (!checkFileExists(fileNameStubs)) { //STUBS FOR TESTING, ITS WORKING WRONG!!!!!!!!
            cardHolder.setStartable();
            /*cardHolder.startQuestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuestClickListener.onQuestReact(quest);
                    hintModule.showHintOnce(R.id.start_ar_hint);
                }
            });*/
        } else {
            cardHolder.setDownloadable();
            /*cardHolder.startQuestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuestClickListener.onDowloadReact(quest);
                    cardHolder.mProgressBar.setVisibility(View.VISIBLE);
                }
            });*/
        }

        questItemMap.put(quest.getId(), position);
        cardHolder.bind(quest, itemClickListenerFromReactor(onItemClickListener));

        setUpHint();
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }

    private void setUpHint() {
        final View startView;
        try {
            int firstQuestItemPosition = questItemMap.get(1);
            startView = fragment
                    .recyclerView
                    .getLayoutManager()
                    .findViewByPosition(firstQuestItemPosition)
                    .findViewById(R.id.start_or_download_quest_btn);
        } catch (NullPointerException e) {
            return;
        }

        int[] pos = new int[2];
        startView.getLocationInWindow(pos);

        hintModule.replaceHint(R.id.select_quest_hint_name, new HintModule.NoCompleteHint() {
            @Override
            public void setUpHint(ShowcaseView sv) {
                sv.setContentText(context.getString(R.string.select_quest_hint_str));
                //Target target = new ViewTarget(startQuestTextView);
                Target target = new ViewTarget(startView);

                int[] pos = new int[2];
                startView.getLocationInWindow(pos);

                sv.setTarget(target);
            }
        });
    }

    private boolean checkFileExists(String filename) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
        return file.exists();
    }

    public void setDownloadProgress(int questId, int downloadProgress) {
        final View downloadedQuestView;
        try {
            int itemPosition = questItemMap.get(questId);
            downloadedQuestView = fragment
                    .recyclerView
                    .getLayoutManager()
                    .findViewByPosition(itemPosition);
                    ProgressBar progressBar = downloadedQuestView.findViewById(R.id.progressDownload);
                    progressBar.setProgress(downloadProgress);
            //notifyItemChanged(itemPosition);
        } catch (NullPointerException e) {
            return;
        }
    }

    public void setDownloadCompleted(final int questId) {
        final View downloadedQuestView;
        try {
            final int itemPosition = questItemMap.get(questId);
            downloadedQuestView = fragment
                    .recyclerView
                    .getLayoutManager()
                    .findViewByPosition(itemPosition);
            ProgressBar progressBar = downloadedQuestView.findViewById(R.id.progressDownload);
            progressBar.setProgress(100);
            /*Button startQuestButton = downloadedQuestView.findViewById(R.id.start_or_download_quest_btn);
            startQuestButton.setText(fragment.getResources().getString(R.string.start_quest_str));
            startQuestButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            startQuestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuestClickListener.onQuestReact(quests.get(itemPosition));
                }
            });*/
            progressBar.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            return;
        }
    }


}
