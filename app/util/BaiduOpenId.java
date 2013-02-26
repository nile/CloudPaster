package util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import play.Play;
import play.libs.WS;
import play.mvc.Router;
import play.mvc.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Black
 * Date: 13-2-25
 * Time: 上午11:08
 */
public class BaiduOpenId {
         static String bd_appkey = Play.configuration.getProperty("openid.bd.appkey","");
    static  String bd_appsecret = Play.configuration.getProperty("openid.bd.appsecret","");
    static String return_to = Play.configuration.getProperty("auth.returnto", Router.getFullUrl("Auth.login"));
    public static String loginUrl(){
       String url = String.format("https://openapi.baidu.com/oauth/2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=basic&display=popup", bd_appkey,return_to);
        return url;
    }

    public static boolean isAuthenticationResponse() {
        return Scope.Params.current()._contains("code");
    }

    public static Map<String, String> userInfo() {
        String code = Scope.Params.current().get("code");
        String access_token_url = String.format("https://openapi.baidu.com/oauth/2.0/token?grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s",code,bd_appkey,bd_appsecret,return_to);

        JsonElement json = WS.url(access_token_url).get().getJson();
        JsonObject obj = (JsonObject) json;
        String access_token = obj.getAsJsonPrimitive("access_token").getAsString();
        String  userInfoUrl = "https://openapi.baidu.com/rest/2.0/passport/users/getInfo?access_token="+access_token;
        JsonObject userinfo = (JsonObject) WS.url(userInfoUrl).get().getJson();
        Map<String,String> info = new HashMap();
        info.put("userid",userinfo.getAsJsonPrimitive("userid").getAsString());
        info.put("username",userinfo.getAsJsonPrimitive("username").getAsString());
        return info;
    }
}
