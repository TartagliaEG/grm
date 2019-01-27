//
//  ViewController.swift
//  GRM-Swift
//
//

import UIKit
import Alamofire
import RxSwift

class GithubRepoListViewController: UIViewController {
    
    var memCache: Dictionary<String, Any>!;
    var repository: GithubRepoListRepositoryContract!;
    var presenter: GithubRepoListPresenterContract!;
    @IBOutlet weak var contentView: GithubRepoListView!
    var dao: GithubRepoDAO!;
    var api: GithubRepoAPI!;
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return UIStatusBarStyle.lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    
        memCache = Dictionary<String, Any>()
        api = GithubRepoAPI()
        dao = GithubRepoDAO()
        repository = GithubRepoListRepository(memCache: memCache, dao: dao, api: api)
        presenter = GithubRepoListPresenter()
        presenter.setup(view: contentView, repository: repository!)
        contentView.setup(presenter: presenter)
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func viewWillAppear(_ animated: Bool) {
        presenter?.start()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        presenter?.stop()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
      
    }
    
    @IBAction func didTouchRequestRepositories (recognized: UITapGestureRecognizer) {
        GithubRepoAPI().getRepoList().subscribe(onSuccess: { (arr) in
            print(arr);
        }) { (err) in
            print(err)
        }
    }
    

}

