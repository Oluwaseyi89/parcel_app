import React from 'react';
import {useState, useEffect} from 'react';

import { colors } from '../config/colors';

import { SceneMap, TabView } from 'react-native-tab-view';
import ViewPagerAdapter from 'react-native-tab-view-viewpager-adapter';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';


import {
    View, Text, StyleSheet, Image, TextInput, Button
} from 'react-native';

import AsyncStorage from '@react-native-async-storage/async-storage';

import CustomerHomeScreen from './customerHome/CustomerHomeScreen';
import CustomerCatalogueScreen from './customerHome/CustomerCatalogueScreen';
import CustomerCartScreen from './customerHome/CustomerCartScreen';
import CustomerHotDealsScreen from './customerHome/CustomerHotDealsScreen';
import CustomerOrdersScreen from './customerHome/CustomerOrdersScreen';
import CustomerDeliveriesScreen from './customerHome/CustomerDeliveriesScreen';
import CustomerResolutionScreen from './customerHome/CustomerResolutionScreen';


const CustomerScreen = ({ navigation }) => {

    const Tab = createMaterialTopTabNavigator();

    return (
        <Tab.Navigator tabBarOptions={{activeTintColor: colors.neonRed, labelStyle: {color: colors.neonRed}}} pager={props => <ViewPagerAdapter {...props}/>} >
            <Tab.Screen  name="customerHome" component={CustomerHomeScreen} options={{title: "Home"}} />
            <Tab.Screen name="customerCatalogue" component={CustomerCatalogueScreen} options={{title: "Catalogue"}} />
            <Tab.Screen name="customerHotDeals" component={CustomerHotDealsScreen} options={{title: "HotDeals"}} />
            <Tab.Screen name="customerCart" component={CustomerCartScreen} options={{title: "Cart"}} />
            <Tab.Screen name="customerOrders" component={CustomerOrdersScreen} options={{title: "Orders"}} />
            <Tab.Screen name="customerDeliveries" component={CustomerDeliveriesScreen} options={{title: "Deliveries"}} />
            <Tab.Screen name="customerResolution" component={CustomerResolutionScreen} options={{title: "Resolution"}} />
        </Tab.Navigator>
    )
}

const styles = StyleSheet.create({
    container: {        
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        alignSelf: "center",
        padding: 45        
    }    
});

export default CustomerScreen