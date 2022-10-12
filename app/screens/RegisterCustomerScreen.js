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


const RegisterCustomerScreen = ({ navigation }) => { 
    
    const [details, setDetails] = useState({
        "first_name": "",
        "last_name": "",
        "country": "",
        "state": "",
        "street": "",
        "phone_no": "",
        "email": "",
        "password": "",
        "reg_date": new Date().toISOString(),
        "is_email_verified": false
    });

    const [retPass, setRetPass] = useState("");

    const { first_name, last_name, country, state, street,
            phone_no, email, password, reg_date, is_email_verified } = details;

    function handleFirstNameChange(first_name) {
        setDetails({ ...details, first_name: first_name });
    }

    function handleLastNameChange(last_name) {
        setDetails({ ...details, last_name: last_name });
    }

    function handleCountryChange(country) {
        setDetails({ ...details, country: country });
    }

    function handleStateChange(state) {
        setDetails({ ...details, state: state });
    }

    function handleStreetChange(street) {
        setDetails({ ...details, street: street });
    }

    function handlePhoneChange(phone_no) {
        setDetails({ ...details, phone_no: phone_no });
    }

    function handleEmailChange(email) {
        setDetails({ ...details, email: email });
    }

    function handlePasswordChange(password) {
        setDetails({ ...details, password: password });
    }

    function handleRetPassChange(retPass) {
        setRetPass(retPass);
    }

    function checkInput() {
        let inputs = [first_name, last_name, country, state, street, 
            phone_no, email, password, retPass];
        
        let count = 0;

        for(let i = 0; i < inputs.length; i++) {
            if(inputs[i] === "") count++;
        } 

        return count;
    }
    
    async function handleCustomerRegistration() {  
        let fetchedIP = "";
        let inputErrorCount = checkInput();  
        await AsyncStorage.getItem("@IPStore:ipAdd")
                .then((res) => {
                    fetchedIP = res;
                }).catch((err) => alert(err.message));  
        
        if(fetchedIP != "" && inputErrorCount == 0 && password == retPass) {

            let routeUrl = "http://" + fetchedIP + ":7000/parcel_customer/reg_customer_mobile/";

            let apiOperation = UseFetchJSON(routeUrl, "POST", details);

            apiOperation.then((res) => {
                if(res.status === "success") {
                    alert(res.data);
                } else if(res.status === "error") {
                    alert(res.data);
                } else {
                    alert("An error occured...");
                }
            }).catch((err) => alert(err.message));

        } else if (password != retPass) {
            alert("Passwords do not match!.");
        } else if (inputErrorCount > 0) {
            let message = inputErrorCount + " input fields are left blank";
            alert(message);
        }
    }

    return (
        <View style={styles.mainContainer}>
            <View style={styles.container}>               
                <Text style={styles.heading}>CUSTOMER REGISTRATION FORM</Text>
                <TextInput style={styles.textInput} placeholder="First Name" value={first_name} onChangeText={handleFirstNameChange} textContentType='givenName'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Last Name" value={last_name} onChangeText={handleLastNameChange} textContentType='familyName'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Country" value={country} onChangeText={handleCountryChange} textContentType='countryName'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="State/Province" value={state} onChangeText={handleStateChange} textContentType='addressState'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Street Address" value={street} onChangeText={handleStreetChange} textContentType='fullStreetAddress'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Phone Number" value={phone_no} onChangeText={handlePhoneChange} textContentType='telephoneNumber'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="E-Mail Address" value={email} onChangeText={handleEmailChange} textContentType='emailAddress'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Password" value={password} onChangeText={handlePasswordChange} textContentType='password' secureTextEntry={true}/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Retype Password" value={retPass} onChangeText={handleRetPassChange} textContentType='password' secureTextEntry={true}/>
                <View style={styles.underLine} />
                <TouchableOpacity onPress={handleCustomerRegistration}>
                    <Text style={styles.buttonDefault}>Register</Text>
                </TouchableOpacity>              
            </View>            
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
    underLine: {
        height: 1,
        backgroundColor: colors.neonRed,
        marginTop: 0
    },
    textInput: {
        fontSize: 15,
        marginTop: 10,
        marginBottom: -12,
        textAlign: "center"
    },
    buttonDefault: {
        color: colors.pearlWhite,
        backgroundColor: colors.neonRed,
        textAlign: "center",
        borderRadius: 3,
        height: 30,
        paddingTop: 5,
        marginTop: 55
    },
    heading: {
        textAlign: "center",
        marginTop: 35,
        marginBottom: 10,
        fontWeight: "bold",
        fontSize: 18,
        color: colors.neonRed
    }    
});

export default RegisterCustomerScreen