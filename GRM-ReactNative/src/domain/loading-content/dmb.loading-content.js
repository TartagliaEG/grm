import React from 'react';
import { StyleSheet, Text, View, ActivityIndicator } from 'react-native';
import THEME, {combine} from '../../styles/styles';
import PropTypes from 'prop-types';
import * as MSG from '../../constants/i18n';

const theme = THEME();
const styles = StyleSheet.create({
  loadingText: combine(theme.contentPrimary, {textAlign: 'center', marginTop: 4}),
  ...theme
});

class DmbLoadingContent extends React.Component {
  render() {    
    return (
      <View>
        <ActivityIndicator size="large" color={styles.contentPrimary.color} />
        <Text style={styles.loadingText}>{this.props.message}</Text>
      </View>
    );
  }
}

DmbLoadingContent.propTypes = {
  message: PropTypes.string
}

DmbLoadingContent.defaultProps = {
  message: MSG.MSG_LOADING()
}

export default DmbLoadingContent;