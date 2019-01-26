package com.tartagliaeg.grmjava.domain.utils;


import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class DataSource<T> {
  private final String mSource;
  private final T mData;

  private static final String NETWORK = "NETWORK";
  private static final String MEMORY = "MEMORY";
  private static final String DATABASE = "DATABASE";
  private static final String NONE = "NONE";

  private DataSource(String source, T data) {
    this.mSource = source;
    this.mData = data;
  }

  public static <NewType> DataSource<NewType> empty(NewType emptyRepresentation) {
    return new DataSource<>(NONE, emptyRepresentation);
  }

  public static <NewType> DataSource<NewType> empty() {
    return new DataSource<>(NONE, null);
  }

  public static <NewType> DataSource<NewType> from(DataSource source, NewType newType) {
    return new DataSource<>(source.mSource, newType);
  }

  public static <NewType> Single<DataSource<NewType>> memory(Single<NewType> data) {
    return data.flatMap(new Function<NewType, SingleSource<? extends DataSource<NewType>>>() {
      @Override
      public SingleSource<? extends DataSource<NewType>> apply(NewType newType) throws Exception {
        return Single.just(memory(newType));
      }
    });
  }

  public static <NewType> Single<DataSource<NewType>> database(Single<NewType> data) {
    return data.flatMap(new Function<NewType, SingleSource<? extends DataSource<NewType>>>() {
      @Override
      public SingleSource<? extends DataSource<NewType>> apply(NewType newType) throws Exception {
        return Single.just(database(newType));
      }
    });
  }

  public static <NewType> Single<DataSource<NewType>> network(Single<NewType> data) {
    return data.flatMap(new Function<NewType, SingleSource<? extends DataSource<NewType>>>() {
      @Override
      public SingleSource<? extends DataSource<NewType>> apply(NewType newType) throws Exception {
        return Single.just(network(newType));
      }
    });
  }


  public static <NewType> DataSource<NewType> memory(NewType data) {
    return new DataSource<>(MEMORY, data);
  }

  public static <NewType> DataSource<NewType> database(NewType data) {
    return new DataSource<>(DATABASE, data);
  }

  public static <NewType> DataSource<NewType> network(NewType data) {
    return new DataSource<>(NETWORK, data);
  }

  public boolean isEmpty() { return NONE.equals(mSource); }

  public boolean isMemory() { return MEMORY.equals(mSource); }

  public boolean isDatabase() { return DATABASE.equals(mSource); }

  public boolean isNetwork() { return NETWORK.equals(mSource); }

  public T getData() {
    return mData;
  }

}


