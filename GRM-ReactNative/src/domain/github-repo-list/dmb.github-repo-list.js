import React from 'react';
import { StyleSheet, Text, View, Image, FlatList, TouchableOpacity, TouchableWithoutFeedback } from 'react-native';
import THEME, { combine } from '../../styles/styles';
import DmbLoadingContent from '../loading-content/dmb.loading-content';
import * as MSG from '../../constants/i18n';
import PropTypes from 'prop-types';
import { REPOSITORIES_VIEW, LOADING_VIEW, FAILURE_VIEW, PRESENTATION_VIEW } from './cns.github-repo-list';
import * as DS from '../models/data-source';

const theme = THEME();
const styles = StyleSheet.create({
  repositoriesContainer: {},
  loadingContainer: {},
  repositoriesHeaderContainer: { flexDirection: 'row', alignSelf: 'center' },
  presentationContainer: { flex: 1, justifyContent: 'center', margin: 32 },
  presentationHeaderContainer: { flexDirection: 'row', justifyContent: 'center' },
  headerTextContainer: { flex: 1, flexDirection: 'column', justifyContent: 'center' },

  loadingText: combine(theme.contentPrimary, { textAlign: 'center' }),
  ...theme
});


class DmbGithubRepoList extends React.Component {

  constructor(props) {
    super(props);
  }

  
  RepositoryView = (props) => {
    const { repository, index } = props
    
    const cardNumber = `${index}`.padStart(2, '0');

    return <TouchableOpacity onPress={() => props.didSelectRepository(repository)}>
      <View style={{ ...styles.listItem, flexDirection: 'row', ...styles.surfacePrimary }}>
        <View style={{ justifyContent: 'center', paddingRight: 16 }}>
          <Text style={{ ...styles.headingPrimary }}>
            {cardNumber}
          </Text>
        </View>
        <View>
          <Text style={{ ...styles.contentPrimaryBig, fontWeight: 'bold' }}>{repository.name}</Text>
          <Text style={styles.contentPrimary}>{repository.owner}</Text>
        </View>
      </View>
    </TouchableOpacity>
  }

  HeaderView = (props) => {
    return (
      <View style={styles.screenHeader}>
        <Image
          style={{ width: 96, height: 96 }}
          source={require("../../../assets/img_github_512x512.png")}></Image>
        <View style={styles.headerTextContainer}>
          <Text style={styles.headingPrimary}>{props.title}</Text>
          <Text style={styles.contentPrimary}>{props.subtitle}</Text>
        </View>
      </View>
    );
  }

  RepositoryListView = (props) => {
    const { HeaderView, RepositoryView } = this;

    if (props.view !== REPOSITORIES_VIEW)
      return null;

    const { dataSource } = props;
    const title = MSG.TITLE_GITHUB_REPOSITORY();
    const subtitle = dataSource.isNetwork()
      ? MSG.MSG_DATA_ORIGIN_API()
      : dataSource.isDatabase()
        ? MSG.MSG_DATA_ORIGIN_DATABASE()
        : MSG.MSG_DATA_ORIGIN_MEMORY()

    return (
      <View style={styles.screenContent}>
        <HeaderView title={title} subtitle={subtitle}></HeaderView>
        <FlatList
          style={styles.screenContent}
          data={dataSource.data()}
          renderItem={(data) => <RepositoryView {...props} repository={data.item} index={data.index}/>}
        />
      </View>
    )
  }

  PresentationView = (props) => {
    const { HeaderView } = this;
    const { view } = props;

    if (view !== PRESENTATION_VIEW && view !== FAILURE_VIEW)
      return null;

    const title = MSG.TITLE_GITHUB_REPOSITORY();
    const subtitle = view === FAILURE_VIEW
      ? MSG.MSG_LOAD_REPOSITORIES_FAILURE()
      : MSG.MSG_LOAD_REPOSITORIES_MESSAGE();

    return <TouchableWithoutFeedback onPress={props.didRequestRepositories}>
      <View style={styles.screenContentCentralized}>
        <HeaderView title={title} subtitle={subtitle}></HeaderView>
      </View>
    </TouchableWithoutFeedback>
  }

  LoadingView = (props) => props.view !== LOADING_VIEW ? null :
    <View style={styles.screenContentCentralized}>
      <DmbLoadingContent></DmbLoadingContent>
    </View>


  render() {
    const { PresentationView, LoadingView, RepositoryListView } = this;

    return (
      <View style={styles.screen}>
        <PresentationView {...this.props} />
        <LoadingView {...this.props} />
        <RepositoryListView {...this.props} />
      </View>
    );
  }
}

DmbGithubRepoList.propTypes = {
  dataSource: PropTypes.object,
  view: PropTypes.oneOf([PRESENTATION_VIEW, FAILURE_VIEW, LOADING_VIEW, REPOSITORIES_VIEW]),
  didRequestRepositories: PropTypes.func.isRequired,
  didSelectRepository: PropTypes.func.isRequired
}

DmbGithubRepoList.defaultProps = {
  dataSource: DS.empty(),
  view: PRESENTATION_VIEW
}

export default DmbGithubRepoList;
