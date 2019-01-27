//
//  GithubRepoAPI.swift
//  GRM-Swift
//
//

import Foundation
import RxSwift
import Alamofire

class GithubRepoAPI {
    private let REPOSITORIES = "http://api.github.com/repositories";
    
    func getRepoList() -> Single<Array<GithubRepo>> {
        return Single.create(subscribe: { single in
            let req = Alamofire.request(self.REPOSITORIES);
            
            req.responseJSON(completionHandler: { (response) in
                guard response.result.error == nil else {
                    single(.error(response.result.error!));
                    return;
                }
                
                guard let data = response.result.value else {
                    single(.success([]));
                    return;
                }
                
                single(.success(
                    (data as! Array<Dictionary<String, Any>>).map({ item in GithubRepo.init(data: item); })
                ));
            });
            
            return Disposables.create{ req.cancel(); };
        });
    }
}
