import React from 'react';
import { useState, useEffect, useCallback, useReducer } from 'react';

import { RadioGroup } from 'react-native-radio-buttons-group';
import { colors } from '../../config/colors';
import { UseFetch, UseFetchJSON } from '../../utils/useFetch';


import {
    View, Text, StyleSheet, FlatList, Platform, Image, useWindowDimensions, TextInput, TouchableOpacity, ImageBackground, Button
} from 'react-native';

import AsyncStorage from '@react-native-async-storage/async-storage';
import { useNavigation } from '@react-navigation/native';


const CustomerHomeScreen = ({ navigation }) => {   

    const [ipAddress, setIpAddress] = useState("");
    const window = useWindowDimensions();
 
   

    let initialState = {
        products: []
    }

    const getAllProds = useCallback((data)=>{
        return dispatch({
            type: "GET_ALL_PRODUCTS",
            payload: data
        })
    },[]);


    let reducer = (state, action) => {
        if (action.type === "GET_ALL_PRODUCTS") {
            let adjData = action.payload;
            
            return {...state, products:adjData}   
        }

        return state;
    }


    async function fetchIP() {
    //    await AsyncStorage.getItem("@IPStore:ipAdd")
    //     .then((res) => {
    //         setIpAddress(res);
    //     }).catch((err) => alert(err.message)); 

        try {
            let value = await AsyncStorage.getItem("@IPStore:ipAdd");
            setIpAddress(value);
        } catch (err) {
            alert(err.message);
        }
    }

    useEffect(()=> {
        fetchIP();
    }, []);

    useEffect(()=>{  
        if(ipAddress) {
            let apiUrl = "http://" + ipAddress + ":7000/parcel_product/get_prod/";
            
            let data = UseFetchJSON(apiUrl, 'GET');
            data.then((res)=>{
                getAllProds(res.data);
            });
        }   
    },[getAllProds, ipAddress]);

    const [state, dispatch] = useReducer(reducer, initialState);
    
    return (
        <View style={styles.container}>
            <FlatList data={state.products} renderItem={ ({ item }) => (
                <View keyExtractor={item.id} style={styles.productContainer}>
                    <View style={styles.prodImage}>
                        <Image style={{height: "100%", width: "100%"}}  source={{uri: item.prod_photo.replace("localhost", ipAddress)}} resizeMode='stretch'/>
                    </View>

                    <View style={styles.prodDetailContainer}>
                        <Text style={styles.prodDetails}>{item.prod_desc}</Text>
                        <Text style={styles.prodDetails}>{item.prod_name}</Text>
                        <Text style={styles.prodDetails}>{item.prod_model}</Text>
                        <Text style={styles.prodDetails}>Product Rating: ?</Text>
                        <Text style={styles.prodDetails}>{item.prod_price}</Text>
                        <Text style={styles.prodDetails}>{item.prod_qty}</Text>
                    </View>

                    <View style={styles.buttonContainer}>
                        <TouchableOpacity>
                            <Text style={styles.buttonText}>Buy Now</Text>
                        </TouchableOpacity>

                        <TouchableOpacity>
                            <Text style={styles.buttonText}>Add to Cart</Text>
                        </TouchableOpacity>
                
                        <TouchableOpacity>
                            <Text style={styles.buttonText}>Remove</Text>
                        </TouchableOpacity>

                    </View>

                </View>
    )} ItemSeparatorComponent={ Platform.OS !== 'android' && (({ highlighted }) => (
        <View style={styles.separator}/>
    ))}/>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    }, 
    productContainer: {
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-around",
        width: "100%",
        height: 250
    },
    prodImage: {
        height: "100%",
        width: "42%"
    },
    prodDetailContainer: {
        display: "flex",
        flexDirection: "column",
        height: "100%",
        width: "29%"
    },
    prodDetails: {
        width: "100%",
        height: 32,
        marginTop: 5
    },
    separator: {
        height: 3,
        width: "100%",
        backgroundColor: colors.neonRed
    }, 
    buttonContainer: {
        display: "flex",
        flexDirection: "column",
    },
    buttonText: {
        color: colors.neonRed,
        backgroundColor: colors.silver,
        borderRadius: 3,
        marginTop: 15,
        textAlign: "center",
        fontSize: 20
    }
});



export default CustomerHomeScreen