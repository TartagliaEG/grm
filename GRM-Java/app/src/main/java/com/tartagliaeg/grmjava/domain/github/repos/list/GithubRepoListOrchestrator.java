package com.tartagliaeg.grmjava.domain.github.repos.list;

import android.app.Activity;
import android.os.Bundle;

import com.tartagliaeg.grmjava.datasources.GithubRepoAPI;
import com.tartagliaeg.grmjava.setup.GrmJavaApplication;
import com.tartagliaeg.grmjava.domain.utils.PlatformData;

import java.util.HashMap;
import java.util.Map;

public class GithubRepoListOrchestrator implements GithubRepoListContract {
  private final IPresenter mPresenter;
  private final IView mView;
  private final IRepository mRepository;
  private final Map<String, Object> mStash;
  private final PlatformData mPlatformData;

  public GithubRepoListOrchestrator(Activity activity, IView view) {
    // in a production environment, the stash should be something that survives to the configuration change
    mStash = new HashMap<>();

    // in a production environment, this instance would be injected and have platform specific operations
    mPlatformData = new PlatformData();

    GrmJavaApplication app = (GrmJavaApplication) activity.getApplication();

    this.mRepository = new GithubRepoListRepository(
      app.getRetrofitClient().create(GithubRepoAPI.class),
      app.getRoomDatabase().githubRepoDAO(),
      mStash,
      mPlatformData
    );

    this.mPresenter = new GithubRepoListPresenter();
    this.mView = view;
  }

  public void setup() {
    mView.setup(this.mPresenter);
    mPresenter.setup(this.mView, this.mRepository);
  }

  public void start() {
    mPresenter.start();
  }

  public void stop() {
    mPresenter.stop();
  }

  public void saveState(Bundle bundle) {
    mPresenter.saveState(bundle);
  }

  public void restoreState(Bundle bundle) {
    mPresenter.restoreState(bundle);
  }

}
