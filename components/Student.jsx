import TcpSocket from 'react-native-tcp-socket';
import React from "react";
import { View, Text } from 'react-native';

const Student = props => {

	const { navigation } = props;

	const ip = navigation.getParam("ip", "0.0.0.0");
	const bssid = navigation.getParam("bssid", "02:00:00:00:00:00");
	const ssid = navigation.getParam("ssid", "<unknown ssid>");
	const name = navigation.getParam("name", "Unknown name");
	const id = navigation.getParam("id", "00000");

	return (
		<View>
			<Text>Student</Text>
			<Text>
				IP - {ip !== null ? ip : "No ip"}
			</Text>
			<Text>
				BBSID - {bssid !== null ? bssid : "No bssid"}
			</Text>
			<Text>
				SSID - {ssid !== null ? ssid : "No ssid"}
			</Text>
			<Text>
				Name - {name !== null ? name : "Unknowm name"}
			</Text>
			<Text>
				ID/Roll_No - {id !== null ? id : "000000"}
			</Text>
		</View>
	)

};

export default Student;