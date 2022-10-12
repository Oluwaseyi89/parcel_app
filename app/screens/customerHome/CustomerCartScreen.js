import React from 'react';
import { useState, useEffect } from 'react';

import { RadioGroup } from 'react-native-radio-buttons-group';
import { colors } from '../config/colors';
import { UseFetch, UseFetchJSON } from '../utils/useFetch';


import {
    View, Text, StyleSheet, Image, TextInput, TouchableOpacity, ImageBackground, Button
} from 'react-native';

import AsyncStorage from '@react-native-async-storage/async-storage';
import { useNavigation } from '@react-navigation/native';


const CustomerCartScreen = ({ navigation }) => {   
    
    return (
        <View style={styles.contianer}>
            <Text>This is Customer Cart Screen</Text>
        </View>
    )
}

const styles = StyleSheet.create({
    contianer: {
        flex: 1,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center"    
    }
});

export default CustomerCartScreen