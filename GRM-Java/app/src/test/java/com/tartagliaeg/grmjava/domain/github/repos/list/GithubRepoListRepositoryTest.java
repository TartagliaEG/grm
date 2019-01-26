package com.tartagliaeg.grmjava.domain.github.repos.list;

import com.tartagliaeg.grmjava.datasources.GithubRepo;
import com.tartagliaeg.grmjava.datasources.GithubRepoAPI;
import com.tartagliaeg.grmjava.datasources.GithubRepoDAO;
import com.tartagliaeg.grmjava.domain.utils.DataSource;
import com.tartagliaeg.grmjava.domain.utils.PlatformData;
import com.tartagliaeg.grmjava.domain.utils.RxSingleObserver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.internal.schedulers.ImmediateThinScheduler;
import io.reactivex.plugins.RxJavaPlugins;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
public class GithubRepoListRepositoryTest {

  private List<GithubRepo> mRepoList = new ArrayList<>();
  private List<GithubRepo> mEmptyRepoList = new ArrayList<>();

  @Before
  public void before() {
    mRepoList.add(new GithubRepo(1, "GRM_Java", "TartagliaEG"));

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
  public void testGithubReposRetrievalFromAPI() {
    final Map<String, Object> map = Mockito.spy(new HashMap<String, Object>());

    final GithubRepoDAO dao = Mockito.mock(GithubRepoDAO.class);
    Mockito.when(dao.getRepoList()).thenReturn(Single.just(mEmptyRepoList));

    final GithubRepoAPI api = Mockito.mock(GithubRepoAPI.class);
    Mockito.when(api.getRepoList()).thenReturn(Single.just(mRepoList));

    PlatformData data = Mockito.mock(PlatformData.class);
    Mockito.when(data.isMainThread()).thenReturn(true);

    GithubRepoListRepository repo = new GithubRepoListRepository(api, dao, map, data);

    repo.getGithubRepos().subscribe(new RxSingleObserver<DataSource<List<GithubRepoListContract.IGithubRepo>>>() {
      @Override
      public void onSuccess(DataSource<List<GithubRepoListContract.IGithubRepo>> source) {
        // Should've retrieved from network
        Assert.assertTrue(source.isNetwork());

        // Should've tried to retrieve from memory map
        Mockito.verify(map, Mockito.atLeastOnce()).containsKey(Mockito.anyString());

        // Should've tried to retrieve from database
        Mockito.verify(dao, Mockito.atLeastOnce()).getRepoList();

        // Should've tried to retrieve from api
        Mockito.verify(api, Mockito.atLeastOnce()).getRepoList();

        // Should've tried to persist on database
        ArgumentCaptor<GithubRepo[]> daoCaptor = ArgumentCaptor.forClass(GithubRepo[].class);
        Mockito.verify(dao, Mockito.times(1)).persist(daoCaptor.capture());

        GithubRepo[] dbPersistedRepo = daoCaptor.getValue();
        Assert.assertEquals(dbPersistedRepo.length, 1);
        Assert.assertEquals(dbPersistedRepo.length, source.getData().size());
        Assert.assertEquals(source.getData().get(0).getId(), dbPersistedRepo[0].getId());

        // Should've tried to persist on memory cache
        ArgumentCaptor<List<GithubRepo>> mapCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(map, Mockito.atLeastOnce()).put(Mockito.anyString(), mapCaptor.capture());

        List<GithubRepo> mePersistedRepo = mapCaptor.getValue();
        Assert.assertEquals(mePersistedRepo.size(), 1);
        Assert.assertEquals(mePersistedRepo.size(), source.getData().size());
        Assert.assertEquals(source.getData().get(0).getId(), mePersistedRepo.get(0).getId());
      }
    });
  }


  @Test
  public void testGithubReposRetrievalFromDatabase() {
    final Map<String, Object> map = Mockito.spy(new HashMap<String, Object>());

    final GithubRepoDAO dao = Mockito.mock(GithubRepoDAO.class);
    Mockito.when(dao.getRepoList()).thenReturn(Single.just(mRepoList));

    final GithubRepoAPI api = Mockito.mock(GithubRepoAPI.class);
    Mockito.when(api.getRepoList()).thenReturn(Single.just(mEmptyRepoList));

    PlatformData data = Mockito.mock(PlatformData.class);
    Mockito.when(data.isMainThread()).thenReturn(true);

    GithubRepoListRepository repo = new GithubRepoListRepository(api, dao, map, data);

    repo.getGithubRepos().subscribe(new RxSingleObserver<DataSource<List<GithubRepoListContract.IGithubRepo>>>() {
      @Override
      public void onSuccess(DataSource<List<GithubRepoListContract.IGithubRepo>> source) {
        // Should've retrieved from network
        Assert.assertTrue(source.isDatabase());

        // Should've tried to retrieve from memory map
        Mockito.verify(map, Mockito.atLeastOnce()).containsKey(Mockito.anyString());

        // Should've tried to retrieve from database
        Mockito.verify(dao, Mockito.atLeastOnce()).getRepoList();

        // Should NOT have tried to retrieve from API
        Mockito.verify(api, Mockito.never()).getRepoList();

        // Should NOT have tried to persist on database (Since the data come from there)
        Mockito.verify(dao, Mockito.never()).persist(Mockito.any(GithubRepo[].class));

        // Should've tried to persist on memory cache
        ArgumentCaptor<List<GithubRepo>> mapCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(map, Mockito.atLeastOnce()).put(Mockito.anyString(), mapCaptor.capture());

        List<GithubRepo> mePersistedRepo = mapCaptor.getValue();
        Assert.assertEquals(mePersistedRepo.size(), 1);
        Assert.assertEquals(mePersistedRepo.size(), source.getData().size());
        Assert.assertEquals(source.getData().get(0).getId(), mePersistedRepo.get(0).getId());

        // Should have the same data as the database
        Assert.assertEquals(source.getData().get(0).getId(), mRepoList.get(0).getId());
        Assert.assertEquals(source.getData().size(), mRepoList.size());
      }
    });

  }


  @Test
  public void testGithubReposRetrievalFromMemory() {
    final Map<String, Object> map = Mockito.mock(HashMap.class);
    Mockito.when(map.containsKey(Mockito.anyString())).thenReturn(true);
    Mockito.when(map.get(Mockito.anyString())).thenReturn(mRepoList);

    final GithubRepoDAO dao = Mockito.mock(GithubRepoDAO.class);
    Mockito.when(dao.getRepoList()).thenReturn(Single.just(mEmptyRepoList));

    final GithubRepoAPI api = Mockito.mock(GithubRepoAPI.class);
    Mockito.when(api.getRepoList()).thenReturn(Single.just(mEmptyRepoList));

    PlatformData data = Mockito.mock(PlatformData.class);
    Mockito.when(data.isMainThread()).thenReturn(true);

    GithubRepoListRepository repo = new GithubRepoListRepository(api, dao, map, data);

    repo.getGithubRepos().subscribe(new RxSingleObserver<DataSource<List<GithubRepoListContract.IGithubRepo>>>() {
      @Override
      public void onSuccess(DataSource<List<GithubRepoListContract.IGithubRepo>> source) {
        // Should've retrieved from memory
        Assert.assertTrue(source.isMemory());

        // Should've tried to retrieve from the memory map
        Mockito.verify(map, Mockito.atLeastOnce()).containsKey(Mockito.anyString());

        // Should NOT have tried to retrieve from the database
        Mockito.verify(dao, Mockito.never()).getRepoList();

        // Should NOT have tried to retrieve from the memory map
        Mockito.verify(api, Mockito.never()).getRepoList();

        // Should NOT have tried to persist on database
        Mockito.verify(dao, Mockito.never()).persist(Mockito.any(GithubRepo[].class));

        Assert.assertEquals(
          source.getData().get(0).getId(),
          mRepoList.get(0).getId()
        );
      }
    });
  }

}