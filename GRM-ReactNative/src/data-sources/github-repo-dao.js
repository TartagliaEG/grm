import * as Rx from 'rxjs';
import { AsyncStorage } from 'react-native';

const REPOSITORIES_KEY = 'GITHUB.REPOSITORIES.KEY';

export const getGithubRepositories = () => {
  return Rx.Observable.create(observer => {
    AsyncStorage.getItem(REPOSITORIES_KEY).then((data) => {
      observer.next((data && JSON.parse(data)) || []);
      observer.complete();
    }).catch((err) => {
      observer.error(err);
    });
  });
}

export const persistGithubRepositories = (repositories) => {
  return Rx.Observable.create(observer => {

    AsyncStorage.setItem(REPOSITORIES_KEY, JSON.stringify(repositories)).then(() => {
      observer.next();
      observer.complete()
    }).catch((err) => {
      observer.error(err);
    });
  })
}

