import * as API from '../../data-sources/github-repo-api';
import * as DAO from '../../data-sources/github-repo-dao';

import { flatMap, map } from 'rxjs/operators';
import { of } from 'rxjs';
import * as DS from '../models/data-source';


const pipeNetworkRetrieval = flatMap((ds) =>
  ds.isEmpty() || ds.data().length == 0
    ? DS.rxNetwork(API.getGithubRepositories())
    : of(ds)
)

const pipeDatabaseRetrieval = flatMap((ds) =>
  ds.isEmpty() || ds.data().length == 0
    ? DS.rxDatabase(DAO.getGithubRepositories())
    : of(ds)
);

const pipeDatabasePersistence = flatMap((ds) => {
  if (ds.isNetwork() && ds.data().length > 0)
    return DAO.persistGithubRepositories(ds.data()).pipe(map(() => ds));

  return of(ds)
});

export const getGithubRepositories = () => {
  return of(DS.empty())
    .pipe(pipeDatabaseRetrieval)
    .pipe(pipeNetworkRetrieval)
    .pipe(pipeDatabasePersistence);
}
