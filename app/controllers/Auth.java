package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import models.CPRole;
import models.OpenIdInfo;
import models.User;
import play.Logger;
import play.Play;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;
import util.BaiduOpenId;
import util.QQOpenId;
import util.StringUtil;
@With({GlobalUser.class})
public class Auth extends Controller{
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_EMAIL = "email";
	public static void logout() {
		session.clear();
		CloudPaster.questions(0);
	}
	public static void yahoologin() throws UnsupportedEncodingException {
		String returnto = Play.configuration.getProperty("auth.returnto", Router.getFullUrl("Auth.login"));
		if(flash.contains("return.to"))
			returnto+="?return="+URLEncoder.encode(flash.get("return.to"), "utf8");
		if (!OpenID.id("https://me.yahoo.com").required(KEY_EMAIL, "http://axschema.org/contact/email")
				.returnTo(returnto)
				.forRealm(Play.configuration.getProperty("auth.returnto", "Auth.login")).verify()) {
			flash.error("无法连接Yahoo服务器");
			login();
		}
	}
	public static void googlelogin() throws UnsupportedEncodingException {
		String returnto = Play.configuration.getProperty("auth.returnto", Router.getFullUrl("Auth.login"));
		if(flash.contains("return.to"))
			returnto+="?return="+URLEncoder.encode(flash.get("return.to"), "utf8");
		if (!OpenID.id("https://www.google.com/accounts/o8/id").required(KEY_EMAIL, "http://axschema.org/contact/email")
				.returnTo(returnto)
				.forRealm(Play.configuration.getProperty("auth.returnto", "Auth.login")).verify()) {
			flash.error("无法连接Google服务器");
			flash.keep();
			login();
		}
	}

	public static void qq() {
		redirect(QQOpenId.loginUrl());
	}

    public static void baidu(){
        redirect(BaiduOpenId.loginUrl());
    }
	public static void clickpass() throws UnsupportedEncodingException {
		String returnto = Play.configuration.getProperty("auth.returnto", Router.getFullUrl("Auth.login"));
		if(flash.contains("return.to"))
			returnto+="?return="+URLEncoder.encode(flash.get("return.to"), "utf8");
		if (!OpenID.id("http://www.clickpass.com/openid_server").required(KEY_EMAIL, "http://axschema.org/contact/email")
				.returnTo(returnto)
				.forRealm(Play.configuration.getProperty("auth.returnto", "Auth.login")).verify()) {
			flash.error("无法连接ClickPass服务器");
			flash.keep();
			login();
		}
	}
	public static void login() {
		flash.keep("return.to");
		if(QQOpenId.isAuthenticationResponse()) {
			Map<String, String> userinfoMap = QQOpenId.userInfo();
			if(userinfoMap == null) {
				 flash.error("不妙，登陆错误。");
				 login();
			}
			User user = User.createOrGetByOpenid(userinfoMap.get("openid"), "QQ");
			user.setName(userinfoMap.get("nick"));
			user.save();
			session.put(KEY_USER, user.id);
            session.put(KEY_TIMESTAMP, System.currentTimeMillis());  
            CloudPaster.questions(0);
		} else if(BaiduOpenId.isAuthenticationResponse()){
            Map<String, String> userinfoMap = BaiduOpenId.userInfo();
            if(userinfoMap == null) {
                flash.error("不妙，登陆错误。");
                login();
            }
            User user = User.createOrGetByOpenid(userinfoMap.get("userid"), "Baidu");
            user.setName(userinfoMap.get("username"));
            user.save();
            session.put(KEY_USER, user.id);
            session.put(KEY_TIMESTAMP, System.currentTimeMillis());
            CloudPaster.questions(0);
        } else if(OpenID.isAuthenticationResponse()) {
	        // Retrieve the verified id
	        UserInfo userinfo = OpenID.getVerifiedID();
	        if(userinfo == null) {
	              flash.error("不妙，登陆错误。");
	              login();
	        } else {
              String email = userinfo.extensions.get(KEY_EMAIL);
              User user = User.createOrGet(email);
              session.put(KEY_USER, user.id);
              session.put(KEY_TIMESTAMP, System.currentTimeMillis());              
              if(params._contains("return")) {
            	  redirect(params.get("return"));
              }
              CloudPaster.questions(0);
	        }
		}
		render();
	}
	static final String KEY_USER = "user.key";
	public static User getLoginUser() {
		String userid = session.get(KEY_USER);
		if(userid == null)
			return null;
		User user = User.findById(Long.parseLong(userid));
		if(user == null) {
			Logger.error("user has removed %s ", userid);
			session.clear();
		}
		return user;
	}

}
