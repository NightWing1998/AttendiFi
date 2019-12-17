import React from "react";
import { View, TextInput, Text, ScrollView, Button } from "react-native";
import { useField } from "../hooks/index";

const StudentForm = props => {

	const [name, resetName] = useField("text");
	const [id, resetId] = useField("text");

	const resetAll = () => {
		resetName();
		resetId();
	}

	const { navigation } = props;

	const ip = navigation.getParam("ip", "0.0.0.0");
	const bssid = navigation.getParam("bssid", "02:00:00:00:00:00");
	const ssid = navigation.getParam("ssid", "<unknown ssid>");

	return (
		<ScrollView>
			<Text>Student Details form</Text>
			<View>
				<Text>Name</Text>
				<TextInput {...name} autoCompleteType="name" />
			</View>
			<View>
				<Text>ID/Roll_NO</Text>
				<TextInput {...id} />
			</View>
			<Button onPress={() => navigation.navigate("Student", { ip, bssid, ssid, name: name.value, id: id.value })} title="Mark attendance....." />
			<Button onPress={resetAll} title="Reset" color="#aa0000" />
		</ScrollView>
	);
};

export default StudentForm;