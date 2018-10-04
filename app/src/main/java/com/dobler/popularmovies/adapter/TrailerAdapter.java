package com.dobler.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dobler.popularmovies.R;
import com.dobler.popularmovies.model.VideoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    final static String YOUTUBE_BASE_URL = "https://img.youtube.com/vi/";

    private final TrailerAdapterOnClickHandler mClickHandler;

    private final TrailerAdapterOnLongClickHandler mLongClickHandler;

    private List<VideoModel.Result> mDataset;

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler, TrailerAdapterOnLongClickHandler longClickHandler) {
        mClickHandler = clickHandler;
        mLongClickHandler = longClickHandler;
    }

    public void setMovieData(List<VideoModel.Result> movieData) {
        mDataset = movieData;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_recycle_view, parent, false);

        TrailerViewHolder vh = new TrailerViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        String key;
        String title;

        key = mDataset.get(position).getKey();
        title = mDataset.get(position).getName();

        Picasso.get().load(YOUTUBE_BASE_URL + key + "/0.jpg")
                .placeholder(R.drawable.no_img)
                .into(holder.mIvPoster);

        holder.mTvTitle.setText(title);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (null == mDataset) return 0;
        return mDataset.size();
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(VideoModel.Result trailer);
    }

    public interface TrailerAdapterOnLongClickHandler {
        void onLongClick(VideoModel.Result trailer);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mTvTitle;
        private ImageView mIvPoster;


        public TrailerViewHolder(View v) {
            super(v);
            mTvTitle = (TextView) v.findViewById(R.id.iv_details_trailer_title);
            mIvPoster = (ImageView) v.findViewById(R.id.iv_details_trailer_poster);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            VideoModel.Result movieForDay = null;
            movieForDay = mDataset.get(adapterPosition);
            mClickHandler.onClick(movieForDay);
        }

        @Override
        public boolean onLongClick(View v) {

            int adapterPosition = getAdapterPosition();
            VideoModel.Result movieForDay = null;

            movieForDay = mDataset.get(adapterPosition);

            mLongClickHandler.onLongClick(movieForDay);

            return true;
        }
    }

}