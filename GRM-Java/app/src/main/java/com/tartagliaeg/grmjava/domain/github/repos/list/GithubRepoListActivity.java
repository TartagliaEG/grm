package com.tartagliaeg.grmjava.domain.github.repos.list;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tartagliaeg.grmjava.R;

public class GithubRepoListActivity extends AppCompatActivity {

  private GithubRepoListOrchestrator mOrchestrator;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.act_github_repo_list);

    GithubRepoListContract.IView view = this.findViewById(R.id.cmp_github_repo_list);
    mOrchestrator = new GithubRepoListOrchestrator(this, view);
    mOrchestrator.setup();

    if (savedInstanceState != null)
      mOrchestrator.restoreState(savedInstanceState);
  }


  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mOrchestrator.saveState(outState);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mOrchestrator.start();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mOrchestrator.stop();
  }
}
