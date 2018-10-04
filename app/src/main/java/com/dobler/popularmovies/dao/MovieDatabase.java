package com.dobler.popularmovies.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.dobler.popularmovies.model.MovieModel;


@Database(entities = {MovieModel.Result.class}, version = 3, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    public abstract MovieDao daoAccess();
}