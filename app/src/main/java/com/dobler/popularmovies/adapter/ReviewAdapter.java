package com.dobler.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dobler.popularmovies.R;
import com.dobler.popularmovies.model.ReviewModel;

import org.json.JSONObject;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {


    private List<ReviewModel.Result> mDataset = null;

    public void setMovieData(List<ReviewModel.Result> movieData) {
        if (mDataset == null) {
            mDataset = movieData;
        } else {
            for (int i = 0; i < movieData.size(); i++) {
                mDataset.add(movieData.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_recycle_view, parent, false);

        ReviewViewHolder vh = new ReviewViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        String author;
        String content;

        author = mDataset.get(position).getAuthor();
        content = mDataset.get(position).getContent();

        holder.mTVAuthor.setText(author);
        holder.mTvContent.setText(content);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (null == mDataset) return 0;
        return mDataset.size();
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(JSONObject trailer);
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView mTVAuthor;
        private TextView mTvContent;

        public ReviewViewHolder(View v) {
            super(v);
            mTVAuthor = (TextView) v.findViewById(R.id.tv_details_review_author);
            mTvContent = (TextView) v.findViewById(R.id.tv_details_review_content);

        }

    }

}