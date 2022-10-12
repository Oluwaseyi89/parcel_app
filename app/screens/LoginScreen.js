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



const radioButtonsData = [
    {
        id: "1",
        label: "Customer",
        value: "Customer",
        selected: false,
        color: colors.neonRed
    },
    {
        id: "2",
        label: "Vendor",
        value: "Vendor",
        selected: false,
        color: colors.neonRed
    },
    {
        id: "3",
        label: "Courier",
        value: "Courier",
        selected: false,
        color: colors.neonRed
    }
];

const LoginScreen = ({ navigation }) => {
    
    // const navigation = useNavigation();

    const [radioButtons, setRadioButtons] = useState(radioButtonsData);

    const [initalInputs, setInitialInputs] = useState({
        "ipAddress": "",
        "email": "",
        "password": "",
        "category": ""
    });

    const { ipAddress, email, password, category } = initalInputs;

    function handleIPAddChange(ipAdd) {
        setInitialInputs({ ...initalInputs, ipAddress: ipAdd });
    }

    function handleEmailChange(newEmail) {
        setInitialInputs({ ...initalInputs, email: newEmail });
    }


    function handlePasswordChange(newPassword) {
        setInitialInputs({ ...initalInputs, password: newPassword });
    }




    function onPressRadioButton(radioButtonsArray) {
        setRadioButtons(radioButtonsArray);
        for (let i = 0; i < radioButtonsArray.length; i++) {
            if (radioButtonsArray[i].selected === true) {
                setInitialInputs({ ...initalInputs, category: radioButtonsArray[i].value });
            }
        }       
    }

   


    const handleLoginPress = async () => {
        let vendorUrl, customerUrl, courierUrl, fetchedIP, BASE_URL;

        if (ipAddress && email && password && category) {
            await AsyncStorage.getItem("@IPStore:ipAdd")
                .then((res) => {
                    fetchedIP = res;
                }).catch((err) => alert(err.message));

            if (fetchedIP !== undefined) {
                BASE_URL = "http://" + fetchedIP + ":7000/";

                if (category === "Customer") {
                    customerUrl = BASE_URL + "parcel_customer/customer_login_mobile/";
                    let details = {
                        "email": email,
                        "password": password
                    }
                    let apiOperation = UseFetchJSON(customerUrl, "POST", details);
                    apiOperation.then((res) => {
                        if (res.status === "success") {
                            AsyncStorage.setItem("@CustomerData:details", JSON.stringify(res.data)).
                                then(() => {
                                    navigation.navigate("customer");
                                }).catch((err) => {
                                    console.log(err.message);
                                    alert(err.message);
                                });
                        } else if (res.status === "password-error") {
                            alert(res.data + " Visit the website to reset your password");
                        } else if (res.status === "error") {
                            alert(res.data);
                        }
                    }).catch((err) => console.log(err.message));
                }

                if (category === "Courier") {
                    courierUrl = BASE_URL + "parcel_backends/courier_login_mobile/";
                    let details = {
                        "email": email,
                        "password": password
                    }
                    let apiOperation = UseFetchJSON(courierUrl, "POST", details);
                    apiOperation.then((res) => {
                        if (res.status === "success") {
                            AsyncStorage.setItem("@CourierData:details", JSON.stringify(res.data)).
                                then(() => {
                                    navigation.navigate("courier");
                                }).catch((err) => {
                                    console.log(err.message);
                                    alert(err.message);
                                });
                        } else if (res.status === "password-error") {
                            alert(res.data + " Visit the website to reset your password");
                        } else if (res.status === "error") {
                            alert(res.data);
                        }
                    }).catch((err) => console.log(err.message));
                }

                if (category === "Vendor") {
                    vendorUrl = BASE_URL + "parcel_backends/vendor_login_mobile/";                    
                    let details = {
                        "email": email,
                        "password": password
                    }
                    let apiOperation = UseFetchJSON(vendorUrl, "POST", details);
                    apiOperation.then((res) => {
                        if (res.status === "success") {
                            AsyncStorage.setItem("@VendorData:details", JSON.stringify(res.data)).
                                then(() => {
                                    navigation.navigate("vendor");
                                }).catch((err) => {
                                    console.log(err.message);
                                    alert(err.message);
                                });
                        } else if (res.status === "password-error") {
                            alert(res.data + " Visit the website to reset your password");
                        } else if (res.status === "error") {
                            alert(res.data);
                        }
                    }).catch((err) => console.log(err.message));
                }
            }
        } else {
            alert("Enter the required fields")
        }
    }

    const saveIPAdd = async () => {
        try {
            if (ipAddress !== "") {
                await AsyncStorage.setItem("@IPStore:ipAdd", ipAddress);
            }
        } catch (err) {
            alert(err.message);
        }
    }

    async function handleRegButtonPress () {
        let value = "";
        try {
            value = await AsyncStorage.getItem("@IPStore:ipAdd");                       
        } catch (err) {
            alert(err.message);
        }

        console.log(value);

        if(category != "" && value != null) {
            if(category === "Vendor") {
                navigation.navigate('registerVendor');
            } else if (category === "Courier") {
                navigation.navigate('registerCourier');
            } else if (category === "Customer") {
                navigation.navigate('registerCustomer');
            } else alert("Choose a Category, please...");
        } else if (value === null || value === "") {
            alert("Save an IP Address, please...");
        } else if (category === "") {
            alert("Choose a Category, please...");
        }
    }


    return (
        <View style={styles.mainContainer}>
            <ImageBackground source={require('../assets/parcel_bg.png')}>
                <View style={styles.container}>
                    <View>
                        <TextInput onEndEditing={() => saveIPAdd()} value={ipAddress} onChangeText={handleIPAddChange} style={styles.textInput} placeholder='IP Add: 192.168.42.141' />
                        <View style={styles.underLine} />
                    </View>
                    <Image style={styles.logo} source={require('../assets/parcel_ico.png')} resizeMode="contain" />
                    <TextInput style={styles.textInput} value={email} onChangeText={handleEmailChange} placeholder='example@gmail.com' keyboardType='email-address' />
                    <View style={styles.underLine} />
                    <TextInput style={styles.textInput} value={password} onChangeText={handlePasswordChange} placeholder='***********' secureTextEntry={true} textContentType='password' />
                    <View style={styles.underLine} />
                    <View style={styles.logBtnMargin}>
                        <Text style={styles.textView}>Choose Category:</Text>
                        <RadioGroup containerStyle={styles.radioContainer} radioButtons={radioButtons} onPress={onPressRadioButton} />
                    </View>
                    <View style={styles.logBtnMargin}>
                        <TouchableOpacity onPress={handleLoginPress}>
                            <Text style={styles.buttonDefault}>Login</Text>
                        </TouchableOpacity>
                        <View style={styles.chainContainer}>
                            <View style={styles.underLineChain} />
                            <Text style={styles.orText}>Or</Text>
                            <View style={styles.underLineChain} />
                        </View>
                        <TouchableOpacity onPress={handleRegButtonPress}>
                            <Text style={styles.buttonDefault}>Register</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </ImageBackground>
        </View>
    )
}

const styles = StyleSheet.create({
    mainContainer: {
        flex: 1
    },
    container: {
        width: "80%",
        alignSelf: "center",
        height: "100%"
    },
    textInput: {
        fontSize: 14,
        marginTop: 28,
        marginBottom: -12,
        textAlign: "center"
    },
    underLine: {
        height: 1,
        backgroundColor: colors.neonRed,
        marginTop: 0
    },
    buttonDefault: {
        color: colors.neonRed,
        backgroundColor: colors.pearlWhite,
        textAlign: "center",
        borderRadius: 3,
        height: 30,
        paddingTop: 5
    },
    buttonPressed: {
        color: colors.pearlWhite,
        backgroundColor: colors.neonRed
    },
    underLineChain: {
        width: "30%",
        height: 1,
        backgroundColor: colors.pearlWhite
    },
    textView: {
        fontSize: 16
    },
    orText: {
        color: colors.pearlWhite
    },
    chainContainer: {
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        padding: 5
    },
    logo: {
        width: "60%",
        height: 100,
        alignSelf: "center"
    },
    radioContainer: {
        display: "flex",
        alignItems: "flex-start",
        marginBottom: 15
    },
    logBtnMargin: {
        marginTop: 27
    }
});

export default LoginScreen