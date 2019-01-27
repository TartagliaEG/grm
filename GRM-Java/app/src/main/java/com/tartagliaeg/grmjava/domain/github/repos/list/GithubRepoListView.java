package com.tartagliaeg.grmjava.domain.github.repos.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.tartagliaeg.grmjava.R;
import com.tartagliaeg.grmjava.domain.utils.DataSource;

import java.util.List;

public class GithubRepoListView extends LinearLayout implements GithubRepoListContract.IView {
  private static final int PRESENTATION_VIEW = 0;
  private static final int REPOSITORIES_VIEW = 1;
  private static final int LOADING_VIEW = 2;
  private static final int FAILURE_VIEW = 0;


  private IPresenter mPresenter;
  private ViewFlipper mMainFlipper;
  private RecyclerView mRecyclerView;
  private AppCompatTextView mTxtRepositoriesSubtitle;
  private AppCompatTextView mTxtPresentationLoadMessage;


  public GithubRepoListView(Context context) {
    super(context);
    init(context);
  }

  public GithubRepoListView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public GithubRepoListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    inflate(context, R.layout.cmp_github_repo_list, this);

    this.mMainFlipper = this.findViewById(R.id.flpMainContainer);

    this.mTxtRepositoriesSubtitle = this.findViewById(R.id.txtRepositoriesSubtitle);

    View presentationContainer = this.findViewById(R.id.lytPresentationContainer);
    presentationContainer.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mPresenter.didRequestRepos();
      }
    });

    this.mRecyclerView = this.findViewById(R.id.rclRepositoriesList);
    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

    this.mTxtPresentationLoadMessage = this.findViewById(R.id.txtPresentationLoadMessage);

  }


  @Override
  public void setup(IPresenter presenter) {
    this.mPresenter = presenter;
  }


  @Override
  public void showLoadingView() {
    mMainFlipper.setDisplayedChild(LOADING_VIEW);
  }

  @Override
  public void showFailureView() {
    mMainFlipper.setDisplayedChild(FAILURE_VIEW);
    mTxtPresentationLoadMessage.setText(R.string.msg_load_repositories_failure);
  }

  @Override
  public void showPresentationView() {
    mMainFlipper.setDisplayedChild(PRESENTATION_VIEW);
    mTxtPresentationLoadMessage.setText(R.string.msg_load_repositories_message);
  }

  @Override
  public void showRepositoriesView(List<IGithubRepo> repos) {
    mMainFlipper.setDisplayedChild(REPOSITORIES_VIEW);
    mRecyclerView.setAdapter(new RecyclerViewAdapter(
      getContext(),
      repos,
      new OnSelectGithubRepositoryListener() {
        @Override
        public void onSelect(IGithubRepo repo) {
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse(repo.getUrl()));
          getContext().startActivity(intent);
        }
      }
    ));
  }

  @Override
  public void showDataSourceOrigin(DataSource source) {
    mTxtRepositoriesSubtitle.setText(source.isMemory()
      ? getContext().getText(R.string.msg_data_origin_memory)
      : source.isDatabase()
      ? getContext().getText(R.string.msg_data_origin_sqlite)
      : getContext().getText(R.string.msg_data_origin_api)
    );
  }


  private static class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private AppCompatTextView mTxtRepoOwnerName;
    private AppCompatTextView mTxtRepoName;
    private AppCompatTextView mTxtRepoNumber;
    private View mContainerView;

    RecyclerViewHolder(@NonNull View view) {
      super(view);
      mContainerView = view;
      mTxtRepoOwnerName = view.findViewById(R.id.txt_repo_owner_name);
      mTxtRepoName = view.findViewById(R.id.txt_repo_name);
      mTxtRepoNumber = view.findViewById(R.id.txt_repo_number);
    }

    @SuppressLint("DefaultLocale")
    void bind(final GithubRepoListContract.IGithubRepo repo, int index, final OnSelectGithubRepositoryListener listener) {
      mContainerView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          listener.onSelect(repo);
        }
      });
      mTxtRepoOwnerName.setText(repo.getOwnerName());
      mTxtRepoName.setText(repo.getName());
      mTxtRepoNumber.setText(String.format("%02d", index));
    }
  }

  private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private final Context mContext;
    private final List<GithubRepoListContract.IGithubRepo> mRepos;
    private final OnSelectGithubRepositoryListener mListener;

    RecyclerViewAdapter(
      Context context,
      List<GithubRepoListContract.IGithubRepo> repos,
      OnSelectGithubRepositoryListener listener
    ) {
      mContext = context;
      mRepos = repos;
      mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(mContext).inflate(R.layout.cmp_github_repo_list_item, viewGroup, false);
      return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder vh, int index) {
      vh.bind(mRepos.get(index), index, mListener);
    }

    @Override
    public int getItemCount() {
      return mRepos.size();
    }

  }

  public interface OnSelectGithubRepositoryListener {
    void onSelect(IGithubRepo repo);
  }

}