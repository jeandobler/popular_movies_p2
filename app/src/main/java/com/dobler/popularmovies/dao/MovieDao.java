package com.dobler.popularmovies.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dobler.popularmovies.model.MovieModel;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<MovieModel.Result>> findAll();

    @Query("SELECT * FROM movies limit 1")
    MovieModel.Result getOne();

    @Query("SELECT * FROM movies where id=:id limit 1")
    MovieModel.Result getOne(String id);

    @Insert
    void insertOne(MovieModel.Result movies);

    @Delete
    void delete(MovieModel.Result movies);
}
