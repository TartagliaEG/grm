import React from 'react';
import { StatusBar, Platform, SafeAreaView } from 'react-native';
import { StyleSheet, Text, View, Image } from 'react-native';
import SmtGithubRepoList from './src/domain/github-repo-list/smt.github-repo-list';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import githubRepoListReducer from './src/domain/github-repo-list/rdx.github-repo-list';


const reducers = {};

const PATH_GITHUB_REPO_LIST = 'githubRepoList';
reducers[PATH_GITHUB_REPO_LIST] = githubRepoListReducer;

const store = createStore(combineReducers(reducers), {});

export default class App extends React.Component {
  render() {
    StatusBar.setBarStyle('light-content');
    
    if(Platform.OS === 'android')
      StatusBar.setBackgroundColor('#000');

    return (
      <Provider store={store}>
        <SafeAreaView style={styles.container}>
          <SmtGithubRepoList style={{}} statePath={PATH_GITHUB_REPO_LIST} />
        </SafeAreaView>
      </Provider>
    );
  }
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
    paddingTop: StatusBar.currentHeight || 0
  },
});
