package com.tartagliaeg.grmjava.datasources;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GithubRepoAPI {

  @GET("repositories")
  Single<List<GithubRepo>> getRepoList();

}
