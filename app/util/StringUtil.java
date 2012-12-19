package util;

import java.util.HashMap;
import java.util.Map;

public class StringUtil {
	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String[] split = param.split("=");
			String name = split[0];
			String value = split.length>1? split[1]:"";
			map.put(name, value);
		}
		return map;
	}
}
