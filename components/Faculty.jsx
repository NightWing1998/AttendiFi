import TcpSocket from 'react-native-tcp-socket';
import React from "react";
import { View, Text } from 'react-native';

const alphanumeric = "1234567890-=[]{}|;,./:?><~!@$%^&*()_+qwertyuiopasdfghjklzxcvbnm";

const genRandom = len => {
	let s = "";
	for (let i = 0; i < len; i++) {
		let rnd = Math.random() * alphanumeric.length;
		rnd = rnd - rnd % 1;
		s = s.concat(alphanumeric[rnd]);
	}
	return s;
}

const Faculty = props => {

	const { navigation } = props;

	const ip = navigation.getParam("ip", "0.0.0.0");
	const bssid = navigation.getParam("bssid", "02:00:00:00:00:00");
	const ssid = navigation.getParam("ssid", "<unknown ssid>");
	const name = navigation.getParam("name", null);
	const subject = navigation.getParam("subject", null);
	const topic = navigation.getParam("topic", null);
	const date = navigation.getParam("date", null);
	const startTime = navigation.getParam("startTime", null);
	const endTime = navigation.getParam("endTime", null);

	const genTextForBarcode = () => {
		let s = "";
		let encodedIp = "";
		let ipArray = ip.split(".");
		let initChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (let i = 0; i < ipArray.length; i++) {
			encodedIp += ipArray[i] + initChar[i];
		}
		s += "#" + encodedIp;
		let encodedBssid = "";
		let bssidArray = bssid.split(":");
		for (let i = 0; i < bssidArray.length; i++) {
			encodedBssid += bssidArray[i] + initChar[i];
		}
		s += "#" + encodedBssid;
		s += "#" + ssid;
		if (s.length < 64) {
			let firstSalt = s.length / 2 - (s.length / 2) % 1;
			let secondSalt = s.length - firstSalt;
			s = genRandom(firstSalt) + s + "#" + genRandom(secondSalt);
		}
		return s;
	}

	const oneRandom = genTextForBarcode();

	return (
		<View>
			<Text>Faculty</Text>
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
				Name - {name !== null ? name : "Unknown name"}
			</Text>
			<Text>
				Subject - {subject !== null ? subject : "Unknown subject"}
			</Text>
			<Text>
				Topic - {topic !== null ? topic : "Unknown topic"}
			</Text>
			<Text>
				Date - {date !== null ? date : "Unknown date"}
			</Text>
			<Text>
				Time - {startTime !== null && endTime !== null ? `${startTime} to ${endTime}` : "Time error"}
			</Text>
			<Text>
				Barcode Text - {oneRandom}
			</Text>
		</View>
	)

};

export default Faculty;