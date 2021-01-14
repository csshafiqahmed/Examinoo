package com.codestown.examinoo.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.CircleImageView;
import com.codestown.examinoo.model.LeaderBoard;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<LeaderBoard> dataList;
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    public final int TOP_LAYOUT = 2;
    private OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private Context mcontext;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public LeaderboardAdapter(Context context, ArrayList<LeaderBoard> dataList, RecyclerView recyclerView) {

        this.mcontext = context;
        this.dataList = dataList;
        // setHasStableIds(true);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // System.out.println("====on Scroll  dx " + dx + "  ===  dy " + dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                View view = inflater.inflate(R.layout.lyt_leaderboard, parent, false);
                viewHolder = new ItemRowHolder(view);
                break;

            case VIEW_TYPE_LOADING:
                View view1 = inflater.inflate(R.layout.progressbar, parent, false);
                viewHolder = new LoadingVH(view1);
                break;

            case TOP_LAYOUT:
                View view2 = inflater.inflate(R.layout.lb_rank_lyt, parent, false);
                viewHolder = new TopHolder(view2);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, final int position) {
        if (holder1 instanceof ItemRowHolder) {

            ItemRowHolder holder = (ItemRowHolder) holder1;

            final LeaderBoard leaderBoard = dataList.get(position);
            holder.tvScore.setText(leaderBoard.getScore());
            holder.imgProfile.setDefaultImageResId(R.drawable.ic_launcher);
            holder.imgProfile.setImageUrl(leaderBoard.getProfile(), imageLoader);
            holder.tvName.setText(leaderBoard.getName());
         holder.tvRank.setText((leaderBoard.getRank()));
           // holder.tvRank.setText(String.valueOf(position+3));

        } else if (holder1 instanceof LoadingVH) {
            LoadingVH loadingViewHolder = (LoadingVH) holder1;
            loadingViewHolder.progressBar1.setIndeterminate(true);
        } else if (holder1 instanceof TopHolder) {
            TopHolder holder = (TopHolder) holder1;

            int topSize = dataList.get(0).getTopList().size();
            holder.imgRank1.setDefaultImageResId(R.drawable.ic_profile1);
            holder.imgRank2.setDefaultImageResId(R.drawable.ic_profile1);
            holder.imgRank3.setDefaultImageResId(R.drawable.ic_profile1);

            if (topSize == 1)
                Rank1(holder, dataList.get(0).getTopList().get(0));
            else if (topSize == 2) {
                Rank1(holder, dataList.get(0).getTopList().get(0));
                Rank2(holder, dataList.get(0).getTopList().get(1));
            } else if (topSize == 3) {
                Rank1(holder, dataList.get(0).getTopList().get(0));
                Rank2(holder, dataList.get(0).getTopList().get(1));
                Rank3(holder, dataList.get(0).getTopList().get(2));
            }

        }

    }

    public void Rank1(TopHolder holder, LeaderBoard leaderBoard) {
        holder.lytRank1.setVisibility(View.VISIBLE);
        holder.tvRank1.setText(leaderBoard.getName());
        holder.tvScore1.setText(leaderBoard.getScore());
        holder.imgRank1.setImageUrl(leaderBoard.getProfile(), imageLoader);

    }

    public void Rank2(TopHolder holder, LeaderBoard leaderBoard) {
        holder.lytRank2.setVisibility(View.VISIBLE);
        holder.tvRank2.setText(leaderBoard.getName());
        holder.tvScore2.setText(leaderBoard.getScore());
        holder.imgRank2.setImageUrl(leaderBoard.getProfile(), imageLoader);

    }

    public void Rank3(TopHolder holder, LeaderBoard leaderBoard) {
        holder.lytRank3.setVisibility(View.VISIBLE);
        holder.tvRank3.setText(leaderBoard.getName());
        holder.tvScore3.setText(leaderBoard.getScore());
        holder.imgRank3.setImageUrl(leaderBoard.getProfile(), imageLoader);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TOP_LAYOUT;
        else
            return dataList.get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    public void setLoaded() {
        isLoading = false;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public CircleImageView imgProfile;
        //  RelativeLayout topLyt;
        public TextView tvName, tvScore, tvRank;

        public ItemRowHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvName = itemView.findViewById(R.id.tvName);
            //topLyt = itemView.findViewById(R.id.topLyt);
        }
    }

    public class TopHolder extends RecyclerView.ViewHolder {
        TextView tvRank1, tvRank2, tvRank3, tvScore1, tvScore2, tvScore3;
        private CircleImageView imgRank1, imgRank2, imgRank3;
        private RelativeLayout lytRank1, lytRank2, lytRank3;

        public TopHolder(View itemView) {
            super(itemView);
            tvRank1 = itemView.findViewById(R.id.tvRank1);
            tvRank2 = itemView.findViewById(R.id.tvRank2);
            tvRank3 = itemView.findViewById(R.id.tvRank3);
            tvScore1 = itemView.findViewById(R.id.tvScore1);
            tvScore2 = itemView.findViewById(R.id.tvScore2);
            tvScore3 = itemView.findViewById(R.id.tvScore3);
            imgRank1 = itemView.findViewById(R.id.imgRank1);
            imgRank2 = itemView.findViewById(R.id.imgRank2);
            imgRank3 = itemView.findViewById(R.id.imgRank3);
            lytRank1 = itemView.findViewById(R.id.lytRank1);
            lytRank2 = itemView.findViewById(R.id.lytRank2);
            lytRank3 = itemView.findViewById(R.id.lytRank3);
            //topLyt = itemView.findViewById(R.id.topLyt);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {
        ProgressBar progressBar1;

        public LoadingVH(View itemView) {
            super(itemView);
            progressBar1 = itemView.findViewById(R.id.progressBar1);
        }
    }

}
