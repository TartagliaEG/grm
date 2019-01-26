package com.tartagliaeg.grmjava.domain.utils;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public abstract class RxSingleObserver<T> implements SingleObserver<T> {


  @Override
  public void onSubscribe(Disposable d) {

  }

  @Override
  public void onError(Throwable t) {
    if (t instanceof RuntimeException)
      throw (RuntimeException) t;

    throw new RuntimeException(t);
  }

}
