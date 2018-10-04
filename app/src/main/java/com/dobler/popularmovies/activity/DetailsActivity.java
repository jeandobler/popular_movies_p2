package com.dobler.popularmovies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.dobler.popularmovies.R;
import com.dobler.popularmovies.adapter.MoviesAdapter;
import com.dobler.popularmovies.adapter.ReviewAdapter;
import com.dobler.popularmovies.adapter.TrailerAdapter;
import com.dobler.popularmovies.dao.MovieDatabase;
import com.dobler.popularmovies.model.MovieModel;
import com.dobler.popularmovies.model.VideoModel;
import com.dobler.popularmovies.viewModel.MoviesViewModel;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity
        implements TrailerAdapter.TrailerAdapterOnClickHandler,
        TrailerAdapter.TrailerAdapterOnLongClickHandler {

    FloatingActionButton fbFavorite;
    ProgressBar mPbLoading;
    ImageView mIvPosterBackground;


    MovieModel.Result favorited;

    MoviesViewModel model;

    MovieModel.Result movieDetail;

    ListView mLvDetailTrailer;
    RecyclerView mRvTrailer;
    RecyclerView mRvReviews;
    int page = 1;
    TextView mTvFullDescription;
    TextView mTvTitle;
    TextView mTvVoteAverage;
    TextView mTvVoteCount;
    TextView mTvReleaseDate;
    RatingBar mRbVoteAverage;
    private ShareActionProvider mShareActionProvider;
    private MovieDatabase movieDatabase;
    private TrailerAdapter mRvTrailerAdapter;
    private ReviewAdapter mRvReviewAdapter;

    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.youtube_url) + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        mRvTrailer = findViewById(R.id.rv_details_trailers);
        mRvReviews = findViewById(R.id.rv_details_reviews);

        model = ViewModelProviders.of(this).get(MoviesViewModel.class);

        this.setupRecycleView(savedInstanceState);

        mTvFullDescription = findViewById(R.id.tv_details_description);
        mTvVoteAverage = findViewById(R.id.tv_details_vote_average);
        mTvVoteCount = findViewById(R.id.tv_details_vote_count);

        mRbVoteAverage = findViewById(R.id.rb_details_vote_average);

        mIvPosterBackground = findViewById(R.id.iv_poster);

        mPbLoading = findViewById(R.id.pb_details);

        setSupportActionBar(toolbar);
        movieDatabase = Room.databaseBuilder(getApplicationContext(),
                MovieDatabase.class, BaseActivity.DATABASE_NAME).fallbackToDestructiveMigration()
                .build();

        this.populateFields();
        fbFavorite = findViewById(R.id.favorite);
        fbFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Runnable fbClick = new Runnable() {
                    @Override
                    public void run() {
                        if (favorited == null) {
                            movieDatabase.daoAccess().insertOne(movieDetail);
                        } else {
                            movieDatabase.daoAccess().delete(movieDetail);
                        }
                    }
                };

                if (favorited == null) {
                    fbFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.heart)));
                } else {
                    fbFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_cyan)));
                }

                Thread newThreadfbclick = new Thread(fbClick);
                newThreadfbclick.start();


            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Runnable r = new Runnable() {
            @Override
            public void run() {
                favorited = movieDatabase.daoAccess().getOne(movieDetail.getId().toString());

                if (favorited != null) {
                    fbFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.heart)));
                } else {
                    fbFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_cyan)));
                }


            }
        };

        Thread newThread = new Thread(r);
        newThread.start();


    }

    protected void setupRecycleView(Bundle savedInstanceState) {
        mRvTrailer.setHasFixedSize(true);
        mRvTrailer.setLayoutManager(new GridLayoutManager(this, 3));
        int resId = R.anim.layout_animation_fall;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        mRvTrailerAdapter = new TrailerAdapter(this, this);
        mRvTrailer.setAdapter(mRvTrailerAdapter);
        mRvTrailer.setLayoutAnimation(animation);


        mRvReviews.setHasFixedSize(false);
        LinearLayoutManager lin = new LinearLayoutManager(this);
        mRvReviews.setLayoutManager(lin);
        mRvReviewAdapter = new ReviewAdapter();
        mRvReviews.setAdapter(mRvReviewAdapter);

    }

    private void loadMovieDataData(String string) {
        String id = string;

        model.getVideos(id).observe(this, videos -> {

            if (videos != null) {
                mRvTrailerAdapter.setMovieData(videos.getResults());
            } else {
                showErrorMessage();
            }
        });

        model.getReviews(id, "1").observe(this, reviews -> {

            if (reviews != null) {
                mRvReviewAdapter.setMovieData(reviews.getResults());
            } else {
                showErrorMessage();
            }
        });

    }


    public void showDataView() {
        mPbLoading.setVisibility(View.INVISIBLE);
    }

    public void hideData() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    public void showErrorMessage() {

    }


    public void populateFields() {
        Intent intent = getIntent();
        if (intent != null) {

            movieDetail = (MovieModel.Result) intent.getSerializableExtra(getString(R.string.MOVIE_INTENT));
            setTitle(movieDetail.getTitle());
            loadMovieDataData(movieDetail.getId().toString());
            Picasso.get().load(MoviesAdapter.POSTER_URL + movieDetail.getPosterPath())
                    .placeholder(R.drawable.no_img)
                    .into(mIvPosterBackground);


            mTvFullDescription.setText(movieDetail.getOverview());
            mTvVoteAverage.setText(movieDetail.getVoteAverage().toString());
            mTvVoteCount.setText("(" + movieDetail.getVoteCount().toString() + ")");
            mRbVoteAverage.setRating((float) (movieDetail.getVoteAverage() / 2));

            showDataView();
        }


    }

    @Override
    public void onClick(VideoModel.Result trailer) {
        String key = trailer.getKey();
        watchYoutubeVideo(this, key);
    }


    @Override
    public void onLongClick(VideoModel.Result trailer) {
        String key = trailer.getKey();
        String youtubeTitle = trailer.getName();
        Intent webIntent = new Intent(Intent.ACTION_SEND);
        webIntent.setType("text/plain");
        webIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                youtubeTitle);
        webIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.youtube_url) + key);

        startActivity(Intent.createChooser(webIntent, "Share via"));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
