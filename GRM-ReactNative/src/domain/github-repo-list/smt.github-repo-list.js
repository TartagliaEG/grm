import { getViewState, getGithubRepos, ACT_REPOSITORIES_RETRIEVAL, ACT_REPOSITORIES_RETRIEVAL_COMPLETED, ACT_REPOSITORIES_RETRIEVAL_FAILED } from './rdx.github-repo-list';
import * as Repository from './rep.github-repo-list';
import { connect } from 'react-redux';
import GithubRepoList from './dmb.github-repo-list';
import { LOADING_VIEW, FAILURE_VIEW, PRESENTATION_VIEW, REPOSITORIES_VIEW } from './cns.github-repo-list';
import { Linking } from 'expo';


const mapStateToProps = (state, ownProps) => ({
  view: getViewState(state[ownProps.statePath]),
  dataSource: getGithubRepos(state[ownProps.statePath])
});

const mapDispatchToProps = (dispatch) => ({
  didRequestRepositories: () => {
    dispatch(ACT_REPOSITORIES_RETRIEVAL(LOADING_VIEW));

    Repository.getGithubRepositories().subscribe(
      ds => dispatch(ACT_REPOSITORIES_RETRIEVAL_COMPLETED(REPOSITORIES_VIEW, ds)),
      er => dispatch(ACT_REPOSITORIES_RETRIEVAL_FAILED(FAILURE_VIEW))
    );

  },
  didSelectRepository: (repository) => {
    Linking.canOpenURL(repository.url).then(supported => {
      if (supported)
        Linking.openURL(repository.url);
      else
        dispatch(ACT_REPOSITORIES_RETRIEVAL_FAILED(FAILURE_VIEW));
    });
  }
});

export default connect(mapStateToProps, mapDispatchToProps)(GithubRepoList);
