package com.tartagliaeg.grmjava.setup;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GrmJavaApplication extends Application {
  private Retrofit mRetrofitClient;
  private GrmRoomDatabase mRoomDatabase;

  @Override
  public void onCreate() {
    super.onCreate();

    mRoomDatabase = GrmRoomDatabase.create(this);

    mRetrofitClient = new Retrofit.Builder()
      .baseUrl("https://api.github.com")
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build();

  }

  public Retrofit getRetrofitClient() {
    return this.mRetrofitClient;
  }
  public GrmRoomDatabase getRoomDatabase() { return this.mRoomDatabase; }
}
