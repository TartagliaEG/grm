package com.tartagliaeg.grmjava.domain.github.repos.list;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.tartagliaeg.grmjava.domain.utils.DataSource;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class GithubRepoListPresenter implements GithubRepoListContract.IPresenter {
  private static final String TAG = GithubRepoListPresenter.class.getSimpleName();
  private static final String ARG_STATE = TAG + ".STATE";

  private IView mView;
  private IRepository mRepository;
  private CompositeDisposable mDisposables;
  private ViewState mState;

  @Override
  public void setup(IView view, IRepository repository) {
    mRepository = repository;
    mView = view;
    mState = new ViewState();
  }

  @Override
  public void didRequestRepos() {
    startRepositoriesRetrieval();
  }

  @Override
  public void restoreState(Bundle bundle) {
    mState = bundle.getParcelable(ARG_STATE);
  }

  @Override
  public void saveState(Bundle bundle) {
    bundle.putParcelable(ARG_STATE, mState);
  }


  @Override
  public void start() {
    mDisposables = new CompositeDisposable();

    if (mState.mVisibleView == ViewState.PRESENTATION)
      showPresentationView();

    else if (mState.mVisibleView == ViewState.FAILURE)
      showFailureView();

    else
      startRepositoriesRetrieval();
  }

  @Override
  public void stop() {
    mDisposables.dispose();
  }


  private void startRepositoriesRetrieval() {
    mRepository.getGithubRepos().subscribe(new SingleObserver<DataSource<List<IGithubRepo>>>() {
      @Override
      public void onSubscribe(Disposable d) {
        mDisposables.add(d);
        showLoadingView();
      }

      @Override
      public void onSuccess(DataSource<List<IGithubRepo>> source) {
        showRepositoriesView(source);
      }

      @Override
      public void onError(Throwable e) {
        showFailureView();
      }
    });
  }

  private void showPresentationView() {
    mState.mVisibleView = ViewState.PRESENTATION;
    mView.showPresentationView();
  }

  private void showLoadingView() {
    mState.mVisibleView = ViewState.LOADING;
    mView.showLoadingView();
  }

  private void showRepositoriesView(DataSource<List<IGithubRepo>> source) {
    mState.mVisibleView = ViewState.REPOSITORIES;
    mView.showDataSourceOrigin(source);
    mView.showRepositoriesView(source.getData());
  }

  private void showFailureView() {
    mState.mVisibleView = ViewState.FAILURE;
    mView.showFailureView();
  }

  @SuppressWarnings("WeakerAccess")
  private static class ViewState implements Parcelable {
    static final int PRESENTATION = 0;
    static final int REPOSITORIES = 1;
    static final int LOADING = 2;
    static final int FAILURE = 3;

    int mVisibleView = PRESENTATION;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeInt(this.mVisibleView);}

    public ViewState() {}

    protected ViewState(Parcel in) {this.mVisibleView = in.readInt();}

    public static final Creator<ViewState> CREATOR = new Creator<ViewState>() {
      @Override
      public ViewState createFromParcel(Parcel source) {return new ViewState(source);}

      @Override
      public ViewState[] newArray(int size) {return new ViewState[size];}
    };
  }
}
