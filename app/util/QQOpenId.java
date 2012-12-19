package util;

import java.util.Map;

import com.ning.http.client.Request;

import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Router;
import play.mvc.Scope.Params;

public class QQOpenId {
	public static boolean isAuthenticationResponse() {
		return Params.current()._contains("openid");
	}
	public static Map<String, String>  userInfo() {
		String returnto = Play.configuration.getProperty("auth.returnto", Router.getFullUrl("Auth.login"));
		String qq_appkey = Play.configuration.getProperty("openid.qq.appkey","");
		String qq_appsecret = Play.configuration.getProperty("openid.qq.appsecret","");
		String str_url = String.format("https://open.t.qq.com/cgi-bin/oauth2/access_token?client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code&code=%s", 
				qq_appkey , qq_appsecret , returnto, Params.current().get("code"));
		WSRequest url = WS.url(str_url);
		try {
			HttpResponse httpResponse = url.getAsync().get();
			Map<String, String> queryMap = StringUtil.getQueryMap(httpResponse.getString());
			return queryMap;
		}catch(Exception e) {
			Logger.error(e, "QQ userinfo error");
		}
		return null;
	}
	public static String loginUrl() {
		String returnto = Play.configuration.getProperty("auth.returnto", Router.getFullUrl("Auth.login"));
		String qq_appkey = Play.configuration.getProperty("openid.qq.appkey","");
		String url_temp = "https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s";
		return String.format(url_temp, qq_appkey,returnto);
	}
}
