//
//  GithubRepoListRepository.swift
//  GRM-Swift
//
//

import Foundation
import RxSwift
import RxSwiftExt

class GithubRepoListRepository: GithubRepoListRepositoryContract {
    let storageKey: String = "GithubRepoListRepositoryContract.repositories";
    
    let dao: GithubRepoDAO;
    let api: GithubRepoAPI;
    var map: Dictionary<String, Any>;
    
    init(memCache: Dictionary<String, Any>, dao: GithubRepoDAO, api: GithubRepoAPI) {
        self.dao = dao;
        self.api = api;
        self.map = memCache;
    }
    
    //private func getFromMemo
    
    func pipeNetworkRetrieval(_ stream: Observable<DataSource<Array<GithubRepo>>>) -> Observable<DataSource<Array<GithubRepo>>> {
        return stream.flatMap {
            return $0.data != nil && $0.data!.count > 0
                ? Observable.just($0)
                : DataSource<Array<GithubRepo>>.network(self.api.getRepoList().asObservable())
        }
    }
    
    func pipeDatabaseRetrieval(_ stream: Observable<DataSource<Array<GithubRepo>>>) -> Observable<DataSource<Array<GithubRepo>>> {
        return stream.flatMap {
            return $0.data != nil && $0.data!.count > 0
                ? Observable.just($0)
                : DataSource<Array<GithubRepo>>.database(self.dao.getRepoList().asObservable())
        }
    }
    
    func pipeMemoryRetrieval(_ stream: Observable<DataSource<Array<GithubRepo>>>) -> Observable<DataSource<Array<GithubRepo>>> {
        return stream.flatMap {
            return $0.data != nil && $0.data!.count > 0
                ? Observable.just($0)
                : Observable.just(DataSource<Array<GithubRepo>>.memory(
                    self.map[self.storageKey] != nil ? self.map[self.storageKey] as! Array<GithubRepo> : Array<GithubRepo>()
                ))
        }
    }
    
    func pipeMemoryPersistence(_ stream: Observable<DataSource<Array<GithubRepo>>>) -> Observable<DataSource<Array<GithubRepo>>> {
        return stream.flatMap { source  -> Observable<DataSource<Array<GithubRepo>>> in
            self.map[self.storageKey] = source.data
            return Observable.just(source);
        }
    }
    
    func pipeDatabasePersistence(_ stream: Observable<DataSource<Array<GithubRepo>>>) -> Observable<DataSource<Array<GithubRepo>>> {
        return stream.flatMap {source  -> Observable<DataSource<Array<GithubRepo>>> in
            if source.data != nil && source.data!.count > 0 && source.isNetwork() {
                try self.dao.persist(repos: source.data!)
            }
            
            return Observable.just(source)
        }
    }


    func getRepoList() -> Single<DataSource<Array<GithubRepo>>> {
        return Single.just(DataSource<Array<GithubRepo>>.empty())
            .asObservable()
            .apply(pipeMemoryRetrieval)
            .apply(pipeDatabaseRetrieval)
            .apply(pipeNetworkRetrieval)
            .apply(pipeMemoryPersistence)
            .apply(pipeDatabasePersistence)
            .asSingle();
    }
    
    
}
