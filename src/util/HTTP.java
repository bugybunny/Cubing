package util;

import java.util.HashMap;
import java.util.Map;

public class HTTP {

	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String[] splits = param.split("=");
			String name = splits[0];
			String value = splits[1];
			map.put(name, value);
		}
		return map;
	}

}
