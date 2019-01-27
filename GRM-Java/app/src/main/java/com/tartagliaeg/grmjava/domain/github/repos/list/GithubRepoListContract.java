package com.tartagliaeg.grmjava.domain.github.repos.list;

import android.os.Bundle;

import com.tartagliaeg.grmjava.domain.utils.DataSource;

import java.util.List;

import io.reactivex.Single;

public interface GithubRepoListContract {

  interface IView extends GithubRepoListContract {
    void setup(IPresenter presenter);
    void showPresentationView();
    void showRepositoriesView(List<IGithubRepo> repos);
    void showLoadingView();
    void showFailureView();
    void showDataSourceOrigin(DataSource source);
  }

  interface IPresenter extends GithubRepoListContract {
    void setup(IView view, IRepository repository);
    void start();
    void stop();
    void saveState(Bundle bundle);
    void restoreState(Bundle bundle);

    void didRequestRepos();
  }

  interface IRepository extends GithubRepoListContract {
    Single<DataSource<List<IGithubRepo>>> getGithubRepos();
  }

  interface IGithubRepo {
    String getName();
    String getOwnerName();
    String getUrl();
    long getId();
  }

}
