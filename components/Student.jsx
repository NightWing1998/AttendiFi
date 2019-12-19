import axios from "axios";
import React, { useState } from "react";
import { View, Text, ActivityIndicator } from 'react-native';

import QRCodeScanner from 'react-native-qrcode-scanner';

const Student = props => {

	const { navigation } = props;

	const ip = navigation.getParam("ip", "0.0.0.0");
	const bssid = navigation.getParam("bssid", "02:00:00:00:00:00");
	const mac = navigation.getParam("mac", "02:00:00:00:00:00");
	const ssid = navigation.getParam("ssid", "<unknown ssid>");
	const name = navigation.getParam("name", "Unknown name");
	const id = navigation.getParam("id", "00000");

	const initChar = "ABCDGHIJKLMNOPQRSTUVWXYZ";

	const [res, setRes] = useState(null);
	const [read, setRead] = useState(false);

	const barcodeRead = e => {
		setRead(true);
		const decodedData = e.data.split("#");
		const index = decodedData[1];
		let encodedIp = decodedData[2];
		let decodedIp = "";
		let encodedBssid = decodedData[3];
		let decodedBssid = "";
		let i = 5;
		while (encodedBssid !== undefined && encodedBssid !== "") {
			let temp = encodedBssid.split(initChar[i]);
			if (temp[0] === "") {
				encodedBssid = undefined;
			} else {
				decodedBssid += temp[0] + (temp[1] !== "" ? ":" : "");
			}
			encodedBssid = temp[1];
			i++;
		}
		i = 0;
		while (encodedIp !== undefined && encodedIp !== "") {
			let temp = encodedIp.split(initChar[i]);
			if (temp[0] === "") {
				encodedIp = undefined;
			} else {
				decodedIp += temp[0] + (temp[1] !== "" ? "." : "");
			}
			encodedIp = temp[1];
			i++;
		}
		if (ssid !== decodedData[4] || bssid !== decodedBssid) {
			setRes("You are not on the same network as your faculty. Please ensure that you and your faculty are on the same network");
			return;
		}
		// console.log(`http://${decodedIp}:8888`);
		axios.post(`http://${decodedIp}:8888`, { index, name, id, mac })
			.then(response => {
				setRes(JSON.stringify({ decodedIp, decodedBssid, data: response.data }));
			})
			.catch(err => { console.log("!!,Error", err.response.data); setRes(JSON.stringify(err.response.data)) });
	}

	return (
		<View>
			<Text>Student</Text>
			{read !== true ?
				<QRCodeScanner
					onRead={barcodeRead}
					topContent={
						<View>
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
								MAC - {mac !== null ? mac : "no mac"}
							</Text>
						</View>
					}
					bottomContent={
						<View>
							<Text>
								Name - {name !== null ? name : "Unknowm name"}
							</Text>
							<Text>
								ID/Roll_No - {id !== null ? id : "000000"}
							</Text>
						</View>
					}
				/>
				:
				res === null ?
					<ActivityIndicator size="large" color="#0000ff" />
					:
					<Text>{res}</Text>
			}
		</View>
	)

};

export default Student;