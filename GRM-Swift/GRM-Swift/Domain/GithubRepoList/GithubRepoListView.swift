//
//  GithubRepoListView.swift
//  GRM-Swift
//
//

import UIKit

@IBDesignable
class GithubRepoListView: UIView, GithubRepoListViewContract, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet var contentView: UIView!
    @IBOutlet var repositoriesView: UIView!
    @IBOutlet var presentationView: UIView!
    @IBOutlet var loadingView: UIView!
    @IBOutlet weak var presentationMessage: UITextView!
    @IBOutlet weak var txtRepositoriesDataOrigin: UITextView!
    @IBOutlet weak var tblRepositoryList: UITableView!
    weak var presenter: GithubRepoListPresenterContract!;
    var repositories: Array<GithubRepo>?;
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setup()
    }
    
    @IBAction func didTapPresentationView(_ sender: Any) {
        presenter.didRequestRepositories();
    }
    
    private func setup() {
        contentView = loadViewFromNib()
        contentView.frame = bounds
        contentView.autoresizingMask = [UIViewAutoresizing.flexibleWidth, UIViewAutoresizing.flexibleHeight]
        addSubview(contentView)
        tblRepositoryList.delegate = self
        tblRepositoryList.dataSource = self;
        tblRepositoryList.register(UINib(nibName: "GithubRepoListTableCell", bundle: nil), forCellReuseIdentifier: "GithubRepoListTableCell")
    }
    
    func loadViewFromNib() -> UIView! {
        let bundle = Bundle(for: type(of: self))
        let nib = UINib(nibName: String(describing: type(of: self)), bundle: bundle)
        let view = nib.instantiate(withOwner: self, options: nil)[0] as! UIView
        return view
    }
    
    func setup(presenter: GithubRepoListPresenterContract) {
        self.presenter = presenter;
    }
    
    func showPresentationView() {
        presentationView.removeFromSuperview()
        repositoriesView.removeFromSuperview()
        loadingView.removeFromSuperview()
        
        contentView.addSubview(presentationView)
        contentView.addConstraints(stretchChildToParentConstraints(child: presentationView, parent: contentView))

        presentationMessage.text =  NSLocalizedString("msg_load_repositories_message", comment: "")
    }
    
    func showRepositoriesView(_ repos: Array<GithubRepo>) {
        presentationView.removeFromSuperview()
        repositoriesView.removeFromSuperview()
        loadingView.removeFromSuperview()
        
        contentView.addSubview(repositoriesView)
        contentView.addConstraints(stretchChildToParentConstraints(child: repositoriesView, parent: contentView))
        repositories = repos;
        
        tblRepositoryList.delegate = self;
        tblRepositoryList.reloadData()
    }
    
    func showLoadingView() {
        presentationView.removeFromSuperview();
        repositoriesView.removeFromSuperview();
        loadingView.removeFromSuperview();
        
        contentView.addSubview(loadingView);
        contentView.addConstraints(stretchChildToParentConstraints(child: loadingView, parent: contentView));
    }
    
    func showFailureView() {
        presentationView.removeFromSuperview()
        repositoriesView.removeFromSuperview()
        loadingView.removeFromSuperview()
        
        contentView.addSubview(presentationView)
        contentView.addConstraints(stretchChildToParentConstraints(child: presentationView, parent: contentView))
        
        presentationMessage.text =  NSLocalizedString("msg_load_repositories_failure", comment: "")
    }
    
    func showDataSourceOrigin(_ source: DataSource<Array<GithubRepo>>) {
        txtRepositoriesDataOrigin.text = source.isDatabase()
            ? NSLocalizedString("msg_data_origin_realm", comment: "")
            : source.isNetwork()
            ? NSLocalizedString("msg_data_origin_api", comment: "")
            : NSLocalizedString("msg_data_origin_memory", comment: "")
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1;
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return repositories == nil ? 0 : repositories!.count;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "GithubRepoListTableCell") as! GithubRepoListTableCell
        cell.bindRepository(repository: repositories![indexPath.item], idx: indexPath.item)
        return cell
    }
    
    
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let url = repositories![indexPath.item].url as String
        
        if #available(iOS 10.0, *) {
            UIApplication.shared.open(URL(string: url)!, options: [:], completionHandler: nil);
        } else {
            UIApplication.shared.openURL(URL(string: url)!);
        }
    }

    
    private func stretchChildToParentConstraints(child: UIView, parent: UIView) -> Array<NSLayoutConstraint> {
        return [
            NSLayoutConstraint(item: child, attribute: NSLayoutAttribute.bottom, relatedBy: NSLayoutRelation.equal, toItem: parent, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0),
            NSLayoutConstraint(item: child, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: parent, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0),
            NSLayoutConstraint(item: child, attribute: NSLayoutAttribute.right, relatedBy: NSLayoutRelation.equal, toItem: parent, attribute: NSLayoutAttribute.right, multiplier: 1, constant: 0),
            NSLayoutConstraint(item: child, attribute: NSLayoutAttribute.left, relatedBy: NSLayoutRelation.equal, toItem: parent, attribute: NSLayoutAttribute.left, multiplier: 1, constant: 0)
        ];
    }

}
