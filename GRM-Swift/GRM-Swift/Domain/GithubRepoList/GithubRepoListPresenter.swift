//
//  GithubRepoListPresenter.swift
//  GRM-Swift
//
//

import Foundation
import RxSwift

class GithubRepoListPresenter: GithubRepoListPresenterContract {
    private let PRESENTATION = 0;
    private let REPOSITORIES = 1;
    private let LOADING = 2;
    private let FAILURE = 3;
    
    private weak var repository: GithubRepoListRepositoryContract?;
    private weak var view: GithubRepoListViewContract?;
    private var disposable : Disposable?;
    private var visibleView: Int;
    
    init() {
        self.visibleView = self.PRESENTATION;
    }
    
    func setup(view: GithubRepoListViewContract, repository: GithubRepoListRepositoryContract) {
        self.view = view;
        self.repository = repository;
    }
    
    func start() {
        if self.visibleView == self.PRESENTATION {
            self.showPresentationView();
            
        } else if self.visibleView == self.FAILURE {
            self.showFailureView();
        
        } else {
            self.didRequestRepositories();
        }
    }
    
    func stop() {
        disposable?.dispose();
    }


    func didRequestRepositories() {
        showLoadingView();
        
        self.disposable = self.repository!.getRepoList().subscribe(
            onSuccess: {data in self.showRepositoriesView(source: data)},
            onError: {err in self.showFailureView()}
        );
    }
    
    private func showPresentationView() {
        self.view!.showPresentationView();
    }
    
    private func showLoadingView() {
        self.view!.showLoadingView();
    }
    
    private func showRepositoriesView(source: DataSource<Array<GithubRepo>>) {
        self.view!.showDataSourceOrigin(source);
        self.view!.showRepositoriesView(source.data!);
    }
    
    private func showFailureView() {
        self.view!.showFailureView();
    }
    
}
