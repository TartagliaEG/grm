package com.tartagliaeg.grmjava.domain.utils;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class Pipes {
  private final PlatformData mPlatformData;

  public Pipes(PlatformData data) {
    this.mPlatformData = data;
  }

  /**
   * Move to main thread ONLY WHEN IT IS NOT ON IT ALREADY.
   * It is used to workaround the default behavior of observeOn method which
   * postpone the stream execution even when you are asking to move to the
   * current thread.
   *
   * @param dummyData - Some dummy data to use in case of exceptions
   * @param <Data>    - The data flowing through the stream
   * @return
   */
  public <Data> SingleTransformer<Data, Data> toMainThread(final Data dummyData) {
    final ThrowableReference reference = new ThrowableReference();

    return new SingleTransformer<Data, Data>() {
      @Override
      public SingleSource<Data> apply(Single<Data> upstream) {
        return upstream.onErrorResumeNext(new Function<Throwable, SingleSource<? extends Data>>() {
          @Override
          public SingleSource<? extends Data> apply(Throwable throwable) throws Exception {
            reference.mThrowable = throwable;
            return Single.just(dummyData);

          }
        }).flatMap(new Function<Data, SingleSource<? extends Data>>() {
          @Override
          public SingleSource<? extends Data> apply(Data data) throws Exception {
            if (reference.mThrowable == null)
              if (!mPlatformData.isMainThread())
                return Single.just(data).observeOn(AndroidSchedulers.mainThread());
              else
                return Single.just(data);

            return (!mPlatformData.isMainThread()
              ? Single.just(data).observeOn(AndroidSchedulers.mainThread())
              : Single.just(data)
            ).flatMap(new Function<Data, SingleSource<? extends Data>>() {
              @Override
              public SingleSource<? extends Data> apply(Data data) throws Exception {
                if (reference.mThrowable instanceof RuntimeException)
                  throw (RuntimeException) reference.mThrowable;

                throw new RuntimeException(reference.mThrowable);
              }
            });
          }
        });
      }
    };
  }


  private static class ThrowableReference {
    private Throwable mThrowable;
  }
}
