import httpBridge from "react-native-http-bridge";
import React, { useState, useEffect } from "react";
import { View, Text, Button, ScrollView } from 'react-native';

let RNFS = require('react-native-fs');

import QRCode from 'react-qr-code';

const alphanumeric = "1234567890-=[]{}|;,./:?><~!@$%^&*()_+qwertyuiopasdfghjklzxcvbnm";

const genRandom = len => {
	let s = "";
	for (let i = 0; i < len; i++) {
		let rnd = Math.random() * alphanumeric.length;
		rnd = rnd - rnd % 1;
		s = s.concat(alphanumeric[rnd]);
	}
	return s;
};

const macDict = new Map();

const Faculty = props => {

	const { navigation } = props;

	const ip = navigation.getParam("ip", "0.0.0.0");
	const bssid = navigation.getParam("bssid", "02:00:00:00:00:00");
	const mac = navigation.getParam("mac", "02:00:00:00:00:00");
	const ssid = navigation.getParam("ssid", "<unknown ssid>");
	const name = navigation.getParam("name", null);
	const subject = navigation.getParam("subject", null);
	const topic = navigation.getParam("topic", null);
	const date = navigation.getParam("date", null);
	const startTime = navigation.getParam("startTime", null);
	const endTime = navigation.getParam("endTime", null);

	const [students, setStudents] = useState([]);

	const [serverStart, setStart] = useState(false);

	const genTextForBarcode = (sLength) => {
		let s = sLength.toString();
		let encodedIp = "";
		let ipArray = ip.split(".");
		let initChar = "ABCDGHIJKLMNOPQRSTUVWXYZ";
		for (let i = 0; i < ipArray.length; i++) {
			encodedIp += ipArray[i] + initChar[i];
		}
		s += "#" + encodedIp;
		let encodedBssid = "";
		let bssidArray = bssid.split(":");
		for (let i = 0; i < bssidArray.length; i++) {
			encodedBssid += bssidArray[i] + initChar[i + 5];
		}
		s += "#" + encodedBssid;
		s += "#" + ssid;
		if (s.length < 128) {
			let firstSalt = s.length / 2 - (s.length / 2) % 1;
			let secondSalt = s.length - firstSalt;
			s = genRandom(firstSalt) + "#" + s + "#" + genRandom(secondSalt);
		}
		console.log("QR reloaded");
		return s;
	}
	const [qr, setqr] = useState(genTextForBarcode(students.length));

	useEffect(() => { macDict.clear() });

	navigation.addListener("didFocus", payload => {

		if (!serverStart) {

			httpBridge.start(8888, "hello", (req) => {

				if (req.type === "POST") {
					switch (req.url.split("/")[1]) {
						case "":
							console.log("@@", JSON.parse(req.postData), macDict, students);
							let data = JSON.parse(req.postData);
							let index = parseInt(data.index);
							let studentMac = data.mac;
							console.log(studentMac, index);
							if (index !== students.length) {
								console.log("index, student length mismatch", index, students.length);
								httpBridge.respond(req.requestId, 400, "application/json", JSON.stringify({ message: "Proxy detected length" }));
							}
							else if (macDict.has(studentMac)) {
								console.log("Student already present", macDict);
								httpBridge.respond(req.requestId, 400, "application/json", JSON.stringify({ message: "Proxy detected mac" }));
							} else {
								console.log("##");
								httpBridge.respond(req.requestId, 200, "application/json", "{\"message\": \"marked\"}");
								console.log("Marked", data, "attended");
								macDict.set(studentMac, true);
								console.log(macDict);
								setStudents([...students, { name: data.name, id: data.id, mac: studentMac }]);
								setqr(genTextForBarcode(students.length));
							}
							break;
						default:
							httpBridge.respond(req.requestId, 404, "application/json", "{\"message\": \"route not found\"}");
							break;
					}
				}

			});

			setStart(true);
		}

	});

	navigation.addListener("willBlur", () => {
		if (serverStart) {
			closeServer();
		}
	});

	const closeServer = () => {
		macDict.clear();
		httpBridge.stop();
		let path = RNFS.DocumentDirectoryPath + "/att-" + name + "-" + date + ".csv";
		let writeAbleContent = "roll no/id,name\n";
		for (let i in students) {
			console.log("$$", i)
			writeAbleContent += `${students[i].id},${students[i].name}\n`;
		}
		RNFS.writeFile(path, writeAbleContent, 'utf8')
			.then((success) => {
				console.log('FILE WRITTEN!', path, success, writeAbleContent);
				setStart(false);
				setStudents([]);
			})
			.catch((err) => {
				console.log(err.message);
			});
	}

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
				MAC - {mac !== null ? mac : "no mac"}
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
			{
				serverStart ?
					<View>
						{qr !== null ?
							<QRCode value={qr} style={{ padding: 10 }} />
							:
							<></>
						}
						<Button onPress={closeServer} title="Close Attendace" style={{ padding: 10 }} />
						<ScrollView>
							{students.map(s => <View key={s.id}>
								<Text >
									{s.name} - {s.id}
								</Text>
							</View>)}
						</ScrollView>
					</View>
					:
					<></>
			}
		</View>
	)

};

export default Faculty;