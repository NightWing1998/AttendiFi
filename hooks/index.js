import {
	useState
} from "react";

export const useField = type => {
	const [value, setValue] = useState('');

	const onChangeText = text => setValue(text);

	const onReset = () => setValue('');

	const reset = () => setValue('');

	return [{
		type,
		value,
		onChangeText,
		onReset
	}, reset];
};

export default {
	useField
};