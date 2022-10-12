import React from 'react';
import { useState, useEffect } from 'react';

import { RadioGroup } from 'react-native-radio-buttons-group';
import { colors } from '../config/colors';
import { UseFetch, UseFetchJSON } from '../utils/useFetch';

import { SceneMap, TabView } from 'react-native-tab-view';
import ViewPagerAdapter from 'react-native-tab-view-viewpager-adapter';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';


import {
    View, Text, StyleSheet, Image, TextInput, TouchableOpacity, ImageBackground, Button
} from 'react-native';

import AsyncStorage from '@react-native-async-storage/async-storage';
import { useNavigation } from '@react-navigation/native';

import VendorProductScreen from './vendorHome/VendorProductScreen';
import VendorDealScreen from './vendorHome/VendorDealScreen';
import VendorTransactionScreen from './vendorHome/VendorTransactionScreen';
import VendorResolutionScreen from './vendorHome/VendorResolutionScreen';
import VendorNotificationScreen from './vendorHome/VendorNotificationScreen';

const VendorScreen = ({ navigation }) => {   
    const Tab = createMaterialTopTabNavigator();

    return (
        <Tab.Navigator  tabBarOptions={{activeTintColor: colors.neonRed, labelStyle: {color: colors.neonRed}}} pager={props => <ViewPagerAdapter {...props}/>}>
            <Tab.Screen  name="vendorProduct" component={VendorProductScreen} options={{title: "Products"}} />
            <Tab.Screen  name="vendorDeal" component={VendorDealScreen} options={{title: "Deals"}} />
            <Tab.Screen  name="vendorTransaction" component={VendorTransactionScreen} options={{title: "Transactions"}} />
            <Tab.Screen  name="vendorResolution" component={VendorResolutionScreen} options={{title: "Resolutions"}} />
            <Tab.Screen  name="vendorNotification" component={VendorNotificationScreen} options={{title: "Notifications"}} />
        </Tab.Navigator>
    )
}

const styles = StyleSheet.create({
    
});

export default VendorScreen