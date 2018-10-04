package com.dobler.popularmovies.network;

import com.dobler.popularmovies.model.MovieModel;
import com.dobler.popularmovies.model.ReviewModel;
import com.dobler.popularmovies.model.VideoModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MovieNetwork {

    //@TODO ADD YOUR KEY HERE
    private static final String api_key = "";


    private static final String MOVIEDB_BASE_URL= "https://api.themoviedb.org/3/";

    public Retrofit getBuilder() {
        return new Retrofit.Builder()
                .baseUrl(MOVIEDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public interface VideoService {
        @GET("movie/{id}/videos?api_key=" + api_key)
        Call<VideoModel> listVideos(@Path("id") String id);
    }

    public interface ReviewService {
        @GET("movie/{id}/reviews?api_key=" + api_key)
        Call<ReviewModel> listReviews(@Path("id") String id, @Query("page") String page);
    }

    public interface MoviesService {
        @GET("movie/{type}?api_key=" + api_key)
        Call<MovieModel> listMovies(@Path("type") String type);
    }


}
