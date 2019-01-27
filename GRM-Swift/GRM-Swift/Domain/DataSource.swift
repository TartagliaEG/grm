//
//  DataSource.swift
//  GRM-Swift
//
//

import Foundation
import RxSwift

@objc enum DataSourceOrigin: Int {
    case NETWORK
    case MEMORY
    case DATABASE
    case NONE
}

class DataSource<T> {
    private let _source: DataSourceOrigin;
    private let _data: T?;
    
    var data: T? { get { return self._data } }
    
    init(source: DataSourceOrigin, data: T?) {
        self._source = source;
        self._data = data;
    }
    
    static func empty<NewType>(_ emptyRepresentation: NewType) -> DataSource<NewType> {
        return DataSource<NewType>.init(source: DataSourceOrigin.NONE, data: emptyRepresentation);
    }
    
    static func empty<NewType>() -> DataSource<NewType> {
        return DataSource<NewType>.init(source: DataSourceOrigin.NONE, data: nil);
    }

    static func from<NewType>(_ source: DataSource,_  newType: NewType) -> DataSource<NewType> {
        return DataSource<NewType>.init(source: source._source, data: newType);
    }

    static func memory<NewType>(_ data: NewType) -> DataSource<NewType> {
        return DataSource<NewType>.init(source: DataSourceOrigin.MEMORY, data: data);
    }

    static func database<NewType>(_ data: NewType) -> DataSource<NewType> {
        return DataSource<NewType>.init(source: DataSourceOrigin.DATABASE, data: data);
    }

    static func network<NewType>(_ data: NewType) -> DataSource<NewType> {
        return DataSource<NewType>.init(source: DataSourceOrigin.NETWORK, data: data);
    }

    static func memory<NewType>(_ stream: Single<NewType>) -> Single<DataSource<NewType>> {
        return stream.flatMap { Single.just(self.memory($0));  }
    }

    static func database<NewType>(_ stream: Single<NewType>) -> Single<DataSource<NewType>> {
        return stream.flatMap { Single.just(self.database($0));  }
    }

    static func network<NewType>(_ stream: Single<NewType>) -> Single<DataSource<NewType>> {
        return stream.flatMap { Single.just(self.network($0));  }
    }

    static func memory<NewType>(_ stream: Observable<NewType>) -> Observable<DataSource<NewType>> {
        return stream.flatMap { Observable.just(self.memory($0));  }
    }
    
    static func database<NewType>(_ stream: Observable<NewType>) -> Observable<DataSource<NewType>> {
        return stream.flatMap { Observable.just(self.database($0));  }
    }
    
    static func network<NewType>(_ stream: Observable<NewType>) -> Observable<DataSource<NewType>> {
        return stream.flatMap { Observable.just(self.network($0));  }
    }

    
    func isEmpty() -> Bool { return DataSourceOrigin.NONE == _source; }
    func isMemory() -> Bool { return DataSourceOrigin.MEMORY == _source; }
    func isDatabase() -> Bool { return DataSourceOrigin.DATABASE == _source; }
    func isNetwork() -> Bool { return DataSourceOrigin.NETWORK == _source; }
    
}
