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

import CourierDealScreen from './courierHome/CourierDealScreen';
import CourierDispatchScreen from './courierHome/CourierDispatchScreen';
import CourierTransactionScreen from './courierHome/CourierTransactionScreen';
import CourierResolutionScreen from './courierHome/CourierResolutionScreen';
import CourierNotificationScreen from './courierHome/CourierNotificationScreen';


const CourierScreen = ({ navigation }) => {   
    
    const Tab = createMaterialTopTabNavigator();

    return (
        <Tab.Navigator tabBarOptions={{activeTintColor: colors.neonRed, labelStyle: {color: colors.neonRed}}} pager={props => <ViewPagerAdapter {...props}/>} >
             <Tab.Screen  name="courierDeal" component={CourierDealScreen} options={{title: "Deals"}} />
             <Tab.Screen  name="courierDispatch" component={CourierDispatchScreen} options={{title: "Dispatch"}} />
             <Tab.Screen  name="courierTransaction" component={CourierTransactionScreen} options={{title: "Transaction"}} />
             <Tab.Screen  name="courierResolution" component={CourierResolutionScreen} options={{title: "Resolution"}} />
             <Tab.Screen  name="courierNotification" component={CourierNotificationScreen} options={{title: "Notification"}} />
        </Tab.Navigator>
    )
}

const styles = StyleSheet.create({
    
});

export default CourierScreen