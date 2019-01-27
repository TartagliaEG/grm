//
//  GithubRepo.swift
//  GRM-Swift
//
//

import Foundation
import RealmSwift

class GithubRepo: Object {
    @objc dynamic var id: Int64 = 0;
    @objc dynamic var name: String = "";
    @objc dynamic var ownerName: String = "";
    @objc dynamic var url: String = "";
    
    convenience init (id: Int64, name: String, ownerName: String, url: String) {
        self.init();
        
        self.id = id;
        self.name = name;
        self.ownerName = ownerName;
        self.url = url;
    }
    
    convenience init (data: Dictionary<String, Any>) {
        self.init();
        
        self.id = (data["id"] ?? 0) as! Int64;
        self.name = (data["name"] ?? "") as! String;
        self.ownerName = ((data["owner"] as! Dictionary<String, Any>)["login"] ?? "") as! String;
        self.url = (data["html_url"] ?? "") as! String;
    }
    
    override static func primaryKey() -> String? {
        return "id"
    }
    
}
