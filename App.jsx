import {
	NetworkInfo
} from 'react-native-network-info';
import {
	View,
	Text,
	Platform,
	Button
} from "react-native";
import React, { useState, useEffect } from "react";

import { PERMISSIONS, request, check, RESULTS } from "react-native-permissions";

import FacultyForm from "./components/FacultyForm";
import StudentForm from "./components/StudentForm";
import Faculty from "./components/Faculty";
import Student from "./components/Student";

import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';

const Home = props => {

	const { navigation } = props;

	const [ip, setIp] = useState(null);
	const [bssid, setBssid] = useState(null);
	const [ssid, setSsid] = useState(null);

	const [error, setError] = useState(null);

	const createError = (message, secs) => {
		setError(message);
		setTimeout(() => setError(null), secs * 1000);
	};

	useEffect(() => {

		let requestPermission = [];

		Promise.all([
			check(
				Platform.select({
					android: PERMISSIONS.ANDROID.ACCESS_COARSE_LOCATION,
					ios: PERMISSIONS.IOS.LOCATION_WHEN_IN_USE
				})
			),
			check(
				Platform.select({
					ios: PERMISSIONS.IOS.CAMERA,
					android: PERMISSIONS.ANDROID.CAMERA
				})
			),
			check(
				Platform.select({
					android: PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE
				})
			),
			check(Platform.select({
				android: PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE
			})),
			check(Platform.select({
				android: PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION
			}))
		]).then(([locationStatus, cameraStatus, readStatus, writeStatus, fineLocationStatus]) => {
			if (locationStatus !== RESULTS.GRANTED) {
				requestPermission.push(Platform.select({
					android: PERMISSIONS.ANDROID.ACCESS_COARSE_LOCATION,
					ios: PERMISSIONS.IOS.LOCATION_WHEN_IN_USE
				}));
			}
			if (cameraStatus !== RESULTS.GRANTED) {
				requestPermission.push(Platform.select({
					ios: PERMISSIONS.IOS.CAMERA,
					android: PERMISSIONS.ANDROID.CAMERA
				}));
			}
			if (Platform.OS === "android") {
				if (readStatus !== RESULTS.GRANTED) {
					requestPermission.push(PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE);
				}
				if (writeStatus !== RESULTS.GRANTED) {
					requestPermission.push(PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE);
				}
				if (fineLocationStatus !== RESULTS.GRANTED) {
					requestPermission.push(PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION);
				}
			}
			if (requestPermission.length === 0) {
				return setAll();
			}
			Promise.all(requestPermission.map(p => request(p)))
				.then(([locationStatus, cameraStatus, readWriteStatus]) => {
					console.log("locationStatus", locationStatus, "cameraStatus", cameraStatus, "readWriteStatus", readWriteStatus);
				})
				.catch(err => createError(err, 5))
				.finally(() => setAll());
		}).catch((reason) => console.log(reason));

		const setAll = () => {
			NetworkInfo.getIPAddress().then(ipAddress => {
				console.log("IP:", ipAddress);
				setIp(ipAddress);
			});

			NetworkInfo.getSSID().then(ssid => {
				console.log("SSID:", ssid);
				setSsid(ssid);
			});

			// Get BSSID
			NetworkInfo.getBSSID().then(bssid => {
				console.log("BSSID:", bssid);
				setBssid(bssid);
			});
		}

	});

	return (
		<View>
			<Text>
				{error !== null ? error : <></>}
			</Text>
			<Button title="Faculty" onPress={() => navigation.navigate("FacultyForm", { ip, bssid, ssid })} />
			<Button title="Student" onPress={() => navigation.navigate("StudentForm", { ip, bssid, ssid })} />
		</View>
	)
};

const App = createStackNavigator(
	{
		Home: {
			screen: Home,
			navigationOptions: {
				title: "AttendiFi"
			}
		},
		FacultyForm: {
			screen: FacultyForm,
			navigationOptions: {
				title: "Faculty Details"
			}
		},
		StudentForm: {
			screen: StudentForm,
			navigationOptions: {
				title: "Student Details"
			}
		},
		Faculty: {
			screen: Faculty,
			navigationOptions: {
				title: "Taking Attendance..."
			}
		},
		Student: {
			screen: Student,
			navigationOptions: {
				title: "Marking Attendance"
			}
		}
	},
	{
		initialRouteName: "Home",
		defaultNavigationOptions: {
			headerStyle: {
				backgroundColor: '#024',
			},
			headerTintColor: '#fff',
			headerTitleStyle: {
				fontWeight: 'bold',
				textAlign: 'center',
				fontFamily: 'roboto',
				fontSize: 20
			}
		}
	}
);

export default createAppContainer(App);