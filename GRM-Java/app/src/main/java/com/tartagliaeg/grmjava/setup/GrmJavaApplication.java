package com.tartagliaeg.grmjava.setup;

import android.app.Application;

import retrofit2.Retrofit;

public class GrmJavaApplication extends Application {
  private Retrofit mRetrofitClient;
  private GrmRoomDatabase mRoomDatabase;

  @Override
  public void onCreate() {
    super.onCreate();

    mRoomDatabase = GrmRoomDatabase.create(this);
    mRetrofitClient = GrmApiConfiguration.create(this);

  }

  public Retrofit getRetrofitClient() {
    return this.mRetrofitClient;
  }
  public GrmRoomDatabase getRoomDatabase() { return this.mRoomDatabase; }
}
