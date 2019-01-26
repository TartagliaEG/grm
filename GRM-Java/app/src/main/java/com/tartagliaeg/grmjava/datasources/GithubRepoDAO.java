package com.tartagliaeg.grmjava.datasources;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


import io.reactivex.Single;

@Dao
public interface GithubRepoDAO {

  @Query("SELECT * FROM github_repo")
  Single<List<GithubRepo>> getRepoList();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void persist(GithubRepo[] repos);

}
