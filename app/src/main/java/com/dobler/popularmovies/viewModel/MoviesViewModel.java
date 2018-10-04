package com.dobler.popularmovies.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dobler.popularmovies.dao.MovieDatabase;
import com.dobler.popularmovies.model.MovieModel;
import com.dobler.popularmovies.model.ReviewModel;
import com.dobler.popularmovies.model.VideoModel;
import com.dobler.popularmovies.network.MovieNetwork;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesViewModel extends ViewModel {


    private MutableLiveData<VideoModel> videos;
    private MutableLiveData<MovieModel> movies;
    private MutableLiveData<List<MovieModel.Result>> favorites;
    private MutableLiveData<ReviewModel> reviews;
    private String oldSearch;
    private String oldPage;


    private MovieDatabase movieDatabase;

    public LiveData<VideoModel> getVideos(String id) {
        if (videos == null) {
            videos = new MutableLiveData<VideoModel>();
            loadVideos(id);
        }
        return videos;
    }

    private void loadVideos(String id) {
        MovieNetwork network = new MovieNetwork();

        MovieNetwork.VideoService service =
                network.getBuilder().create(MovieNetwork.VideoService.class);

        Call<VideoModel> callVideo = service.listVideos(id);

        callVideo.enqueue(new Callback<VideoModel>() {
            @Override
            public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                videos.setValue(response.body());
            }

            @Override
            public void onFailure(Call<VideoModel> call, Throwable t) {
            }
        });

    }

    public LiveData<ReviewModel> getReviews(String id, String page) {
        if (reviews == null || page != oldPage) {
            reviews = new MutableLiveData<ReviewModel>();
            loadReviews(id, page);
        }
        return reviews;
    }

    private void loadReviews(String id, String page) {
        MovieNetwork network = new MovieNetwork();

        MovieNetwork.ReviewService service =
                network.getBuilder().create(MovieNetwork.ReviewService.class);

        Call<ReviewModel> callReview = service.listReviews(id, page);

        callReview.enqueue(new Callback<ReviewModel>() {
            @Override
            public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                reviews.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ReviewModel> call, Throwable t) {
            }
        });

    }

    public LiveData<MovieModel> getMovies(String type) {

        if (movies == null || oldSearch != type) {
            oldSearch = type;
            movies = new MutableLiveData<MovieModel>();
            loadMovies(type);
        }
        return movies;
    }

    private void loadMovies(String type) {
        MovieNetwork network = new MovieNetwork();

        MovieNetwork.MoviesService service =
                network.getBuilder().create(MovieNetwork.MoviesService.class);

        Call<MovieModel> callMovies = service.listMovies(type);

        callMovies.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                movies.setValue(response.body());
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
            }
        });

    }

    public LiveData<List<MovieModel.Result>> getFavorites(MovieDatabase movieDatabase) {
        return movieDatabase.daoAccess().findAll();
    }

}

