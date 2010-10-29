import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class TestJson {
public static void main(String[] args) throws IOException {
	
	String str = FileUtils.readFileToString(new File("json.txt"));
	 Object obj=JSONValue.parse(new StringReader(str));
System.out.println(obj.getClass());
JSONObject jo = (JSONObject) obj;
System.out.println(jo.get("content"));
//	Gson gson = new Gson();
//	HashMap fromJson = gson.fromJson(str, HashMap.class);
//	System.out.println(fromJson.values());
//	JSONTokenizer jsonTokenizer = new JSONTokenizer(new StringReader(str)); 
//	 Token nextToken;
//	while(null != (nextToken=jsonTokenizer.nextToken())) {
//		 System.out.println(nextToken);
//	 }
}
}
