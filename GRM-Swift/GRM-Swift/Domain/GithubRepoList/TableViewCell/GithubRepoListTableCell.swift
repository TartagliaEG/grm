//
//  GithubRepoListTableCell.swift
//  GRM-Swift
//
//

import UIKit


class GithubRepoListTableCell: UITableViewCell {
    @IBOutlet weak var cellContentView: UIView!
    @IBOutlet weak var txtRepositoryName: UILabel!
    @IBOutlet weak var txtRepositoryNumber: UILabel!
    @IBOutlet weak var txtRepositoryOwner: UILabel!
    
    func bindRepository(repository: GithubRepo, idx: Int) {
        self.txtRepositoryName.text = repository.name;
        self.txtRepositoryOwner.text = repository.ownerName;
        self.txtRepositoryNumber.text =  NSString.init(format: "%02d", idx) as String
    }
    
   
    override func setHighlighted(_ highlighted: Bool, animated: Bool) {
        if highlighted {
            contentView.backgroundColor = UIColor.init(red: 0.3, green: 0.3, blue: 0.3, alpha: 1)
        } else {
            contentView.backgroundColor = UIColor.black
        }
        
    }
    
    
}
