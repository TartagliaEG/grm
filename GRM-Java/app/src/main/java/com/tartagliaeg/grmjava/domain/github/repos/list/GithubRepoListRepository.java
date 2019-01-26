package com.tartagliaeg.grmjava.domain.github.repos.list;

import com.tartagliaeg.grmjava.datasources.GithubRepo;
import com.tartagliaeg.grmjava.datasources.GithubRepoAPI;
import com.tartagliaeg.grmjava.datasources.GithubRepoDAO;
import com.tartagliaeg.grmjava.domain.utils.DataSource;
import com.tartagliaeg.grmjava.domain.utils.Pipes;
import com.tartagliaeg.grmjava.domain.utils.PlatformData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


@SuppressWarnings("RedundantThrows")
public class GithubRepoListRepository implements GithubRepoListContract.IRepository {
  private static final String STASH_KEY = GithubRepoListRepository.class.getName() + ".REPOS";

  private final GithubRepoAPI mApi;
  private final GithubRepoDAO mDao;
  private final Map<String, Object> mMap;
  private final PlatformData mPlatformData;
  private final Pipes mPipes;

  public GithubRepoListRepository(
    GithubRepoAPI api,
    GithubRepoDAO dao,
    Map<String, Object> stash,
    PlatformData data
  ) {
    this.mApi = api;
    this.mDao = dao;
    this.mMap = stash;
    this.mPlatformData = data;
    this.mPipes = new Pipes(data);
  }

  private List<GithubRepo> getMemoryRepos() {
    //noinspection unchecked
    return mMap.containsKey(STASH_KEY)
      ? (List<GithubRepo>) mMap.get(STASH_KEY)
      : new ArrayList<GithubRepo>();
  }

  private void setMemoryRepos(List<GithubRepo> repos) {
    mMap.put(STASH_KEY, repos);
  }


  private SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>> pipeMemoryRetrieval() {
    return new SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>>() {
      @Override
      public SingleSource<DataSource<List<GithubRepo>>> apply(Single<DataSource<List<GithubRepo>>> upstream) {
        return upstream.flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<? extends DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<? extends DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            // choose the data source that should continue through the stream
            return source.getData().size() > 0
              ? Single.just(source)
              : Single.just(DataSource.memory(getMemoryRepos()));
          }
        });
      }
    };
  }

  private SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>> pipeDatabaseRetrieval() {
    return new SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>>() {
      @Override
      public SingleSource<DataSource<List<GithubRepo>>> apply(Single<DataSource<List<GithubRepo>>> upstream) {
        return upstream.flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<? extends DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<? extends DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            // choose the proper thread
            return source.getData().size() > 0
              ? Single.just(source)
              : Single.just(source).observeOn(Schedulers.io());

          }
        }).flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<? extends DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<? extends DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            // choose the data source that should continue through the stream
            return (source.getData().size() > 0)
              ? Single.just(source)
              : DataSource.database(mDao.getRepoList());
          }
        });
      }
    };
  }

  private SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>> pipeNetworkRetrieval() {
    return new SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>>() {
      @Override
      public SingleSource<DataSource<List<GithubRepo>>> apply(Single<DataSource<List<GithubRepo>>> upstream) {
        return upstream.flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<? extends DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<? extends DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            // choose the proper thread
            return (source.getData().size() > 0)
              ? Single.just(source)
              : Single.just(source).observeOn(Schedulers.io());

          }

        }).flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<? extends DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<? extends DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            // choose the data source that should continue through the stream
            return (source.getData().size() > 0)
              ? Single.just(source) // If the previous call returned data, just returns it
              : DataSource.network(mApi.getRepoList());

          }
        });
      }
    };
  }

  private SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>> pipeDatabasePersistence() {
    return new SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>>() {
      @Override
      public SingleSource<DataSource<List<GithubRepo>>> apply(Single<DataSource<List<GithubRepo>>> upstream) {
        return upstream.flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<? extends DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<? extends DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            // Decides whether the thread should be changed
            return source.isNetwork() && source.getData().size() > 0
              ? Single.just(source).observeOn(Schedulers.io())
              : Single.just(source);

          }
        }).flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            if (source.isNetwork() && source.getData().size() > 0)
              mDao.persist(source.getData().toArray(new GithubRepo[0]));

            return Single.just(source);
          }
        });
      }
    };
  }

  private SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>> pipeMemoryPersistence() {
    return new SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<GithubRepo>>>() {
      @Override
      public SingleSource<DataSource<List<GithubRepo>>> apply(Single<DataSource<List<GithubRepo>>> upstream) {
        return upstream.flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<? extends DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<? extends DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            // choose the proper thread
            return mPlatformData.isMainThread()
              ? Single.just(source)
              : Single.just(source).observeOn(AndroidSchedulers.mainThread());

          }
        }).doOnSuccess(new Consumer<DataSource<List<GithubRepo>>>() {
          @Override
          public void accept(DataSource<List<GithubRepo>> source) throws Exception {
            setMemoryRepos(source.getData());

          }
        });
      }
    };
  }

  private SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<IGithubRepo>>> pipeGithubRepoTransformation() {

    return new SingleTransformer<DataSource<List<GithubRepo>>, DataSource<List<IGithubRepo>>>() {
      @Override
      public SingleSource<DataSource<List<IGithubRepo>>> apply(Single<DataSource<List<GithubRepo>>> upstream) {
        return upstream.flatMap(new Function<DataSource<List<GithubRepo>>, SingleSource<? extends DataSource<List<GithubRepo>>>>() {
          @Override
          public SingleSource<? extends DataSource<List<GithubRepo>>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            // choose the proper thread
            return mPlatformData.isMainThread()
              ? Single.just(source)
              : Single.just(source).observeOn(AndroidSchedulers.mainThread());

          }
        }).map(new Function<DataSource<List<GithubRepo>>, DataSource<List<IGithubRepo>>>() {
          @Override
          public DataSource<List<IGithubRepo>> apply(DataSource<List<GithubRepo>> source) throws Exception {
            List<IGithubRepo> repositories = new ArrayList<>();

            for (final GithubRepo gr : source.getData())
              repositories.add(new IGithubRepo() {
                public String getName() { return gr.getName(); }

                public String getOwnerName() { return gr.getOwnerName(); }

                public long getId() { return gr.getId(); }
              });

            return DataSource.from(source, repositories);
          }
        });
      }
    };
  }


  @Override
  public Single<DataSource<List<IGithubRepo>>> getGithubRepos() {
    return Single
      .just(DataSource.empty(Collections.<GithubRepo>emptyList()))
      .compose(pipeMemoryRetrieval()) // tries to retrieve from memory
      .compose(pipeDatabaseRetrieval()) // tries to retrieve from database (if needed)
      .compose(pipeNetworkRetrieval()) // tries to retrieve from network (if needed)
      .compose(pipeDatabasePersistence()) // caches on database (if needed)
      .compose(pipeMemoryPersistence()) // caches on memory
      .compose(pipeGithubRepoTransformation()) // transforms the output
      .compose(mPipes.toMainThread(DataSource.<List<IGithubRepo>>empty())); // Move the stream to main thread
  }


}
