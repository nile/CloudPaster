package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import play.data.parsing.DataParser;

public class SimpleJsonParser extends DataParser {

	@Override
	public Map<String, String[]> parse(InputStream is) {
		Object obj = JSONValue.parse(new BufferedReader(new InputStreamReader(
				is)));
		JSONObject jo = (JSONObject) obj;
		  Map<String, String[]> result = new HashMap<String, String[]>();
		  Set<Entry<String, String>> entrySet = jo.entrySet();
		for (Entry<String, String> e : entrySet) {
			  putMapEntry(result, e.getKey(), e.getValue());
		  }
		return result;
	}
}
