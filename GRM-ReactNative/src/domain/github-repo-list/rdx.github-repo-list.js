import StateBuilder from '../models/state';
import * as DS from '../models/data-source';

const KEY = 'rdx.github-repo-list';

const PROP_GITHUB_REPO_LIST_VIEW_STATE = "github.repo.list.view.state";
const PROP_GITHUB_REPO_LIST = "github.repo.list";



const TYP_REPOSITORIES_RETRIEVAL = `${KEY}:TYP_REPOSITORIES_RETRIEVAL`;
const ACT_REPOSITORIES_RETRIEVAL = (view) => ({
  type: TYP_REPOSITORIES_RETRIEVAL,
  payload: { view, dataSource: DS.empty() }
});
const RED_REPOSITORIES_RETRIEVAL = (state, action) => new StateBuilder(state)
  .set(PROP_GITHUB_REPO_LIST_VIEW_STATE, action.payload.view)
  .state();

  

const TYP_REPOSITORIES_RETRIEVAL_COMPLETED = `${KEY}:TYP_REPOSITORIES_RETRIEVAL_COMPLETED`;
const ACT_REPOSITORIES_RETRIEVAL_COMPLETED = (view, dataSource) => ({
  type: TYP_REPOSITORIES_RETRIEVAL_COMPLETED,
  payload: { view, dataSource }
});
const RED_REPOSITORIES_RETRIEVAL_COMPLETED = (state, action) => new StateBuilder(state)
  .set(PROP_GITHUB_REPO_LIST_VIEW_STATE, action.payload.view)
  .set(PROP_GITHUB_REPO_LIST, action.payload.dataSource)
  .state();



const TYP_REPOSITORIES_RETRIEVAL_FAILED = `${KEY}:TYP_REPOSITORIES_RETRIEVAL_FAILED`;
const ACT_REPOSITORIES_RETRIEVAL_FAILED = (view) => ({
  type: TYP_REPOSITORIES_RETRIEVAL_FAILED,
  payload: { view }
});
const RED_REPOSITORIES_RETRIEVAL_FAILED = (state, action) => new StateBuilder(state)
  .set(PROP_GITHUB_REPO_LIST_VIEW_STATE, action.payload.view)
  .state();


const REDUCER = (state, action) => {

  if (action.type === TYP_REPOSITORIES_RETRIEVAL)
    return RED_REPOSITORIES_RETRIEVAL(state, action);

  if (action.type === TYP_REPOSITORIES_RETRIEVAL_COMPLETED)
    return RED_REPOSITORIES_RETRIEVAL_COMPLETED(state, action);

  if (action.type === TYP_REPOSITORIES_RETRIEVAL_FAILED)
    return RED_REPOSITORIES_RETRIEVAL_FAILED(state, action);

  return new StateBuilder(state)
    .set(PROP_GITHUB_REPO_LIST, DS.empty())
    .set(PROP_GITHUB_REPO_LIST_VIEW_STATE, undefined)
    .state();
};


export const getViewState = (state) => state[PROP_GITHUB_REPO_LIST_VIEW_STATE];
export const getGithubRepos = (state) => state[PROP_GITHUB_REPO_LIST];
export {
  ACT_REPOSITORIES_RETRIEVAL,
  ACT_REPOSITORIES_RETRIEVAL_COMPLETED,
  ACT_REPOSITORIES_RETRIEVAL_FAILED
};
export default REDUCER;
