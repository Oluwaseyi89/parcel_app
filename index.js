/**
 * @format
 */

import {AppRegistry} from 'react-native';
import ParcelApp from './app/ParcelApp';
import App from './App';
// import ParcelApp from './ParcelApp';
import {name as appName} from './app.json';

// AppRegistry.registerComponent(appName, () => App);
AppRegistry.registerComponent(appName, () => ParcelApp);
