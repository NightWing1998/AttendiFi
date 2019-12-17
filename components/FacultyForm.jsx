import React from "react";
import { View, TextInput, Text, ScrollView, Button } from "react-native";
import { useField } from "../hooks/index";

const FacultyForm = props => {

	const [startTime, resetStartTime] = useField("time");
	const [endTime, resetEndTime] = useField("time");
	const [date, resetDate] = useField("date");

	const [name, resetName] = useField("text");
	const [subject, resetSubject] = useField("text");
	const [topic, resetTopic] = useField("text");

	const resetAll = () => {
		resetDate();
		resetEndTime();
		resetName();
		resetStartTime();
		resetSubject();
		resetTopic();
	}

	const { navigation } = props;

	const ip = navigation.getParam("ip", "0.0.0.0");
	const bssid = navigation.getParam("bssid", "02:00:00:00:00:00");
	const ssid = navigation.getParam("ssid", "<unknown ssid>");

	return (
		<ScrollView>
			<Text>Fill in the details about you and the lecture</Text>
			<View>
				<Text>Name</Text>
				<TextInput {...name} />
			</View>
			<View>
				<Text>Subject</Text>
				<TextInput {...subject} />
			</View>
			<View>
				<Text>Topic</Text>
				<TextInput {...topic} />
			</View>
			<View>
				<Text>Date</Text>
				<TextInput {...date} />
			</View>
			<View>
				<Text>Starting time</Text>
				<TextInput {...startTime} />
			</View>
			<View>
				<Text>Ending time</Text>
				<TextInput {...endTime} />
			</View>
			<Button onPress={() => navigation.navigate("Faculty", { ip, bssid, ssid, name: name.value, subject: subject.value, date: date.value, startTime: startTime.value, endTime: endTime.value, topic: topic.value })} title="Start taking attendance....." />
			<Button onPress={resetAll} title="Reset" color="#aa0000" />
		</ScrollView>
	);
};

export default FacultyForm;