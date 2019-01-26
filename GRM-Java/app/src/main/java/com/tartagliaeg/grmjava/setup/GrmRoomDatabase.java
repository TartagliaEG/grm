package com.tartagliaeg.grmjava.setup;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.tartagliaeg.grmjava.datasources.GithubRepo;
import com.tartagliaeg.grmjava.datasources.GithubRepoDAO;


@Database(entities = {GithubRepo.class}, version = 1)
public abstract class GrmRoomDatabase extends RoomDatabase {

  public static GrmRoomDatabase create(Context context) {
    return Room
      .databaseBuilder(context, GrmRoomDatabase.class, GrmRoomDatabase.class.getSimpleName())
      .build();
  }

  public abstract GithubRepoDAO githubRepoDAO();


}
