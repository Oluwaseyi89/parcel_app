import React from 'react';
import {useState} from 'react';

import {
    View, Text, StyleSheet, Image, TextInput
} from 'react-native';

const ParcelHeader = () => {
    const [searchParam, setSearchParam] = useState("");
    console.log(searchParam)

    const handleSearchParamChange = (product) => {
        setSearchParam(product);
    }

  return (
   <View style={styles.headerContainer}>
       <View style={styles.upperContainer}>
           <Image style={styles.logo} source={require('../assets/parcel_ico.png')}/>
           <TextInput value={searchParam} onChangeText={handleSearchParamChange} style={styles.searchInput} placeholder="Search for products..." />
           <View style={styles.cartBaskContainer}>
               <Image style={styles.cartImg} source={require('../assets/cart_bask_ico.png')}/>
               <View>
                                      
               </View>
           </View>
       </View>
       <View style={styles.lowerContainer}>
           <Text>Lower container</Text>
       </View>
   </View>
  )
}

const styles = StyleSheet.create({
    headerContainer: {
        marginTop: 0,
        borderColor: 'green',
        flexDirection: 'column',
        height: 100,
        justifyContent: 'space-around',
    },
    text: {
        fontSize: 24,
        fontWeight: 'bold'
    },
    upperContainer: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-between',
        borderColor: 'black',
        minWidth: '100%'
    },
    lowerContainer: {
        flex: 1,
        borderColor: 'orange'
    },
    logo: {
        flex: 1,
        height: '40%',
        width: '65%',
        resizeMode: 'contain',
        paddingLeft: 0
    },
    cartImg: {
        flex: 1,
        height: 10,
        width: 35,
        resizeMode: 'contain',
        paddingLeft: 0
    },
    searchInput: {
        borderBottomColor: 'red',
        flex: 1,
    }, 
    cartBaskContainer: {
        flex: 1
    }
});

export default ParcelHeader;