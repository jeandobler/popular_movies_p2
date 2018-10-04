package com.dobler.popularmovies.activity;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dobler.popularmovies.R;
import com.dobler.popularmovies.adapter.MoviesAdapter;
import com.dobler.popularmovies.dao.MovieDatabase;
import com.dobler.popularmovies.model.MovieModel;
import com.dobler.popularmovies.viewModel.MoviesViewModel;

import org.json.JSONArray;

public class MainActivity extends BaseActivity implements MoviesAdapter.MovieDbAdapterOnClickHandler {


    RecyclerView mRvMovie;
    JSONArray mJAMovieList;
    RecyclerView.LayoutManager mRcLayoutManager;
    MoviesAdapter mRvAdapter;
    BottomNavigationView mBottomNavigationView;
    MovieDatabase movieDatabase;
    TextView mTvMessage;
    ProgressBar mPbLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPbLoading = findViewById(R.id.pb_loading);
        mRvMovie = (RecyclerView) findViewById(R.id.rc_main_movie);


        movieDatabase = Room.databaseBuilder(getApplicationContext(),
                MovieDatabase.class, BaseActivity.DATABASE_NAME).fallbackToDestructiveMigration()
                .build();

        mTvMessage = findViewById(R.id.tv_message);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bn_main);
        this.setupRecycleView(savedInstanceState);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        hideDataView();
                        switch (item.getItemId()) {
                            case R.id.menu_sort_popular:
                                loadMoviesFromNetwork(getString(R.string.sort_popular));
                                return true;
                            case R.id.menu_sort_toprated:
                                loadMoviesFromNetwork(getString(R.string.sort_toprated));
                                return true;
                            case R.id.menu_sort_upcoming:
                                loadMoviesFromNetwork(getString(R.string.sort_upcoming));
                                return true;
                            case R.id.menu_sort_favorite:
                                loadMovieDataFromRoom();
                                return true;

                        }
                        return true;
                    }
                });
        mBottomNavigationView.setSelectedItemId(R.id.menu_sort_toprated);

    }


    @Override
    public void onClick(MovieModel.Result movieForDay) {

        Context context = this;
        Class destinationClass = DetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        Bundle b = new Bundle();
        b.putSerializable(getString(R.string.MOVIE_INTENT), movieForDay);
        intentToStartDetailActivity.putExtras(b);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intentToStartDetailActivity,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intentToStartDetailActivity);
        }
    }


    private void loadMoviesFromNetwork(String string) {

        MoviesViewModel model = ViewModelProviders.of(this).get(MoviesViewModel.class);
        model.getMovies(string).observe(this, movies -> {

            if (movies != null) {
                showDataView();

                mRvAdapter.setMovieData(movies.getResults());
            } else {
                showErrorMessage();
            }
        });
    }

    private void loadMovieDataFromRoom() {


        MoviesViewModel model = ViewModelProviders.of(this).get(MoviesViewModel.class);
        model.getFavorites(movieDatabase).observe(this, favorites -> {

            if (favorites != null) {
                showDataView();

                mRvAdapter.setMovieData(favorites);
            } else {
                showErrorMessage();
            }
        });


    }

    protected void setupRecycleView(Bundle savedInstanceState) {
        mRvMovie.setHasFixedSize(true);

        mRvMovie.setLayoutManager(new GridLayoutManager(this, 2));
        mRvMovie.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(4), true));
        int resId = R.anim.layout_animation_fall;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        mRvAdapter = new MoviesAdapter(this);
        mRvMovie.setAdapter(mRvAdapter);
        mRvMovie.setLayoutAnimation(animation);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void hideDataView() {
        mPbLoading.setVisibility(View.VISIBLE);
        mRvMovie.setVisibility(View.INVISIBLE);
    }

    private void showDataView() {
        mPbLoading.setVisibility(View.INVISIBLE);
        mTvMessage.setVisibility(View.INVISIBLE);
        mRvMovie.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mTvMessage.setVisibility(View.VISIBLE);
        mRvMovie.setVisibility(View.INVISIBLE);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}
