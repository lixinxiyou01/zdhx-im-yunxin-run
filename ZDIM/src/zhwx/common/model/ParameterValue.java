package zhwx.common.model;

import java.util.ArrayList;
import java.util.List;

public class ParameterValue {

	private List<String> values = new ArrayList<String>();

	public ParameterValue() {

	}

	public ParameterValue(String val) {
		addValue(val);
	}
	public void addValue(String value) {
		values.add(value);
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	// public String getValues() {
	// String result = "";
	// if (values.size() == 0) {
	// return result;
	// }
	// for (String val : values) {
	// result += val + ",";
	// }
	// result = result.substring(0, result.length() - 1);
	// return result;
	// }
}
