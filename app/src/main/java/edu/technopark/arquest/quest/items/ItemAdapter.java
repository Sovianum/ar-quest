package edu.technopark.arquest.quest.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import edu.technopark.arquest.App;
import edu.technopark.arquest.HintModule;
import edu.technopark.arquest.R;
import edu.technopark.arquest.core.game.Item;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> items;

    @Inject
    HintModule hintModule;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final ItemAdapter.OnItemClickListener onItemClickListener;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_txt)
        TextView nameView;
        @BindView(R.id.description_txt)
        TextView descriptionView;
        @BindView(R.id.item_img)
        ImageView imageView;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Item item, final OnItemClickListener onItemClickListener) {
            hintModule.addHint(R.id.inventory_item_hint, new HintModule.NoCompleteHint() {
                @Override
                public void setUpHint(ShowcaseView sv) {
                    sv.setTarget(new ViewTarget(itemView));
                    sv.setContentText("Для того, чтобы взять предмет в руки, нажмите на него");
                }
            });
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
        App.getAppComponent().inject(this);
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
