package com.tartagliaeg.grmjava.domain.github.repos.list;

import android.os.Bundle;
import android.os.Parcelable;

import com.tartagliaeg.grmjava.domain.utils.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.internal.schedulers.ImmediateThinScheduler;
import io.reactivex.plugins.RxJavaPlugins;

@SuppressWarnings("unchecked")
public class GithubRepoListPresenterTest {

  private List<GithubRepoListContract.IGithubRepo> mRepoList = new ArrayList<>();

  @Before
  public void before() {
    mRepoList.add(new GithubRepoListContract.IGithubRepo() {
      public String getName() { return "grm"; }

      @Override
      public String getUrl() {
        return "https://github.com/TartagliaEG/grm";
      }

      public String getOwnerName() {return "TartagliaEG";}

      public long getId() { return 1;}
    });

    RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
      @Override
      public Scheduler apply(Scheduler scheduler) {
        return ImmediateThinScheduler.INSTANCE;
      }
    });
    RxAndroidPlugins.setMainThreadSchedulerHandler(new Function<Scheduler, Scheduler>() {
      @Override
      public Scheduler apply(Scheduler scheduler) {
        return ImmediateThinScheduler.INSTANCE;
      }
    });

    MockitoAnnotations.initMocks(this);
  }


  @Test
  public void testPresenterSetup() {
    GithubRepoListContract.IRepository repo = Mockito.mock(GithubRepoListContract.IRepository.class);
    Mockito.when(repo.getGithubRepos()).thenReturn(Single.just(DataSource.network(mRepoList)));

    GithubRepoListContract.IView view = Mockito.mock(GithubRepoListContract.IView.class);

    GithubRepoListPresenter presenter = new GithubRepoListPresenter();
    presenter.setup(view, repo);

    // Setup
    Mockito.verify(repo, Mockito.never()).getGithubRepos();
    Mockito.verify(view, Mockito.never()).showLoadingView();
    Mockito.verify(view, Mockito.never()).showRepositoriesView(Mockito.any(List.class));
    Mockito.verify(view, Mockito.never()).showPresentationView();
    Mockito.verify(view, Mockito.never()).showFailureView();
    Mockito.verify(view, Mockito.never()).showDataSourceOrigin(Mockito.any(DataSource.class));
  }

  @Test
  public void testPresenterStartup() {
    GithubRepoListContract.IRepository repo = Mockito.mock(GithubRepoListContract.IRepository.class);
    Mockito.when(repo.getGithubRepos()).thenReturn(Single.just(DataSource.network(mRepoList)));

    GithubRepoListContract.IView view = Mockito.mock(GithubRepoListContract.IView.class);

    GithubRepoListPresenter presenter = new GithubRepoListPresenter();
    presenter.setup(view, repo);

    presenter.start();

    // Should ask the view to show the presentation
    Mockito.verify(view, Mockito.times(1)).showPresentationView();
    Mockito.verify(repo, Mockito.never()).getGithubRepos();
    Mockito.verify(view, Mockito.never()).showLoadingView();
    Mockito.verify(view, Mockito.never()).showRepositoriesView(Mockito.any(List.class));
    Mockito.verify(view, Mockito.never()).showFailureView();
    Mockito.verify(view, Mockito.never()).showDataSourceOrigin(Mockito.any(DataSource.class));

  }

  @Test
  public void testPresenterRepositoriesRetrievalSuccess() {
    DataSource<List<GithubRepoListContract.IGithubRepo>> dataSource = DataSource.network(mRepoList);
    GithubRepoListContract.IRepository repo = Mockito.mock(GithubRepoListContract.IRepository.class);
    Mockito.when(repo.getGithubRepos()).thenReturn(Single.just(dataSource));

    GithubRepoListContract.IView view = Mockito.mock(GithubRepoListContract.IView.class);

    GithubRepoListPresenter presenter = new GithubRepoListPresenter();
    presenter.setup(view, repo);

    presenter.start();
    presenter.didRequestRepos();

    ArgumentCaptor<List> rpCaptor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<DataSource> dsCaptor = ArgumentCaptor.forClass(DataSource.class);

    // Should've tried to retrieve data from IRepository
    Mockito.verify(repo, Mockito.times(1)).getGithubRepos();
    // Should've asked the view to show loading
    Mockito.verify(view, Mockito.times(1)).showLoadingView();
    // Should've asked the view to show the retrieved repositories
    Mockito.verify(view, Mockito.times(1)).showRepositoriesView(rpCaptor.capture());
    // Should've asked the view to show the data source origin
    Mockito.verify(view, Mockito.times(1)).showDataSourceOrigin(dsCaptor.capture());
    // Should NOT have asked the view to show the failure message
    Mockito.verify(view, Mockito.never()).showFailureView();

    Assert.assertEquals(rpCaptor.getValue(), dataSource.getData());
    Assert.assertEquals(dsCaptor.getValue(), dataSource);

  }


  @Test
  public void testPresenterRepositoriesRetrievalFailure() {
    DataSource<List<GithubRepoListContract.IGithubRepo>> data = DataSource.network(mRepoList);
    GithubRepoListContract.IRepository repo = Mockito.mock(GithubRepoListContract.IRepository.class);
    Mockito.when(repo.getGithubRepos()).thenReturn(Single.just(data).map(new Function<DataSource<List<GithubRepoListContract.IGithubRepo>>, DataSource<List<GithubRepoListContract.IGithubRepo>>>() {
      @Override
      public DataSource<List<GithubRepoListContract.IGithubRepo>> apply(DataSource<List<GithubRepoListContract.IGithubRepo>> listDataSource) throws Exception {
        throw new RuntimeException("");
      }
    }));

    GithubRepoListContract.IView view = Mockito.mock(GithubRepoListContract.IView.class);

    GithubRepoListPresenter presenter = new GithubRepoListPresenter();
    presenter.setup(view, repo);

    presenter.start();
    presenter.didRequestRepos();

    // Should've tried to retrieve data from IRepository
    Mockito.verify(repo, Mockito.times(1)).getGithubRepos();
    // Should've asked the view to show loading
    Mockito.verify(view, Mockito.times(1)).showLoadingView();
    // Should've have asked the view to show the failure message
    Mockito.verify(view, Mockito.times(1)).showFailureView();
    // Should NOT have asked the view to show the data source origin
    Mockito.verify(view, Mockito.never()).showDataSourceOrigin(Mockito.any(DataSource.class));
    // Should NOT have asked the view to show the retrieved repositories
    Mockito.verify(view, Mockito.never()).showRepositoriesView(Mockito.any(List.class));

  }


  @Test
  public void testPresenterRepositoriesSaveAndRestoration() {
    DataSource<List<GithubRepoListContract.IGithubRepo>> data = DataSource.network(mRepoList);
    GithubRepoListContract.IRepository repo = Mockito.mock(GithubRepoListContract.IRepository.class);
    Mockito.when(repo.getGithubRepos()).thenReturn(Single.just(data));

    GithubRepoListContract.IView view = Mockito.mock(GithubRepoListContract.IView.class);

    GithubRepoListPresenter presenter = new GithubRepoListPresenter();
    presenter.setup(view, repo);

    Bundle bundle = Mockito.mock(Bundle.class);

    presenter.start();
    presenter.didRequestRepos();
    presenter.saveState(bundle);

    ArgumentCaptor<Parcelable> pcCaptor = ArgumentCaptor.forClass(Parcelable.class);
    Mockito.verify(bundle, Mockito.times(1)).putParcelable(Mockito.anyString(), pcCaptor.capture());

    Parcelable state = pcCaptor.getValue();
    Assert.assertNotNull(state);


    /* ASSERTIONS AFTER DATA RETRIEVAL */
    Mockito.verify(repo, Mockito.times(1)).getGithubRepos();
    Mockito.verify(view, Mockito.times(1)).showLoadingView();
    Mockito.verify(view, Mockito.times(1)).showRepositoriesView(Mockito.any(List.class));
    Mockito.verify(view, Mockito.times(1)).showDataSourceOrigin(Mockito.any(DataSource.class));
    Mockito.verify(view, Mockito.never()).showFailureView();


    /* MOCKS THE PREVIOUS STATE ON BUNDLE */
    Mockito.when(bundle.getParcelable(Mockito.anyString())).thenReturn(state);

    presenter = new GithubRepoListPresenter();
    presenter.setup(view, repo);
    presenter.restoreState(bundle);
    presenter.start();


    /* ASSERTIONS AFTER STATE RESTORATION */
    Mockito.verify(repo, Mockito.times(2)).getGithubRepos();
    Mockito.verify(view, Mockito.times(2)).showLoadingView();
    Mockito.verify(view, Mockito.times(2)).showRepositoriesView(Mockito.any(List.class));
    Mockito.verify(view, Mockito.times(2)).showDataSourceOrigin(Mockito.any(DataSource.class));
    Mockito.verify(view, Mockito.never()).showFailureView();

  }


}