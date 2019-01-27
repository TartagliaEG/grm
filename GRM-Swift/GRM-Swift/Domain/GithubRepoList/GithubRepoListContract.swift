//
//  GithubRepoListContract.swift
//  GRM-Swift
//
//

import Foundation
import RxSwift

protocol GithubRepoListViewContract: class {
    func setup(presenter: GithubRepoListPresenterContract);
    func showPresentationView();
    func showRepositoriesView(_ repos: Array<GithubRepo>);
    func showLoadingView();
    func showFailureView();
    func showDataSourceOrigin(_ source: DataSource<Array<GithubRepo>>);

}
protocol GithubRepoListPresenterContract: class {
    func didRequestRepositories();
    func setup(view: GithubRepoListViewContract,repository: GithubRepoListRepositoryContract);
    func start();
    func stop();
}

protocol GithubRepoListRepositoryContract: class {
    func getRepoList() -> Single<DataSource<Array<GithubRepo>>>;
}
