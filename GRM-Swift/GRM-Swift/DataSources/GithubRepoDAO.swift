//
//  GithubRepoDAO.swift
//  GRM-Swift
//
//

import Foundation
import RealmSwift
import RxSwift

class GithubRepoDAO {
    
    func getRepoList() -> Single<Array<GithubRepo>> {
        return Single.create(subscribe: { single in
            do {
                let realm = try Realm();
                let results = realm.objects(GithubRepo.self)
                
                single(.success(results.map { $0 }))
                
            } catch let err as NSError {
                single(.error(err))
            }
            
            return Disposables.create {  };
        });
    }
    
    func persist(repos:Array<GithubRepo>) throws {
        let realm = try Realm();
        
        try realm.write {
            for r in repos {
                realm.add(r, update: true);
            }
        }
    }
    
}
