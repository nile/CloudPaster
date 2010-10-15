package controllers;

import models.User;
import play.Play;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;

public class Auth extends Controller{
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_EMAIL = "email";
	public static void logout() {
		session.clear();
		CloudPaster.index();
	}
	public static void yahoologin() {
		if (!OpenID.id("https://me.yahoo.com").required(KEY_EMAIL, "http://axschema.org/contact/email")
				.returnTo(Play.configuration.getProperty("auth.returnto", "Auth.login"))
				.forRealm(Play.configuration.getProperty("auth.returnto", "Auth.login")).verify()) {
			flash.error("Oops. Cannot contact yahoo");
			login();
		}
	}
	public static void googlelogin() {
		if (!OpenID.id("https://www.google.com/accounts/o8/id").required(KEY_EMAIL, "http://axschema.org/contact/email")
				.returnTo(Play.configuration.getProperty("auth.returnto", "Auth.login"))
				.forRealm(Play.configuration.getProperty("auth.returnto", "Auth.login")).verify()) {
			flash.error("Oops. Cannot contact google");
			login();
		}
	}
	public static void login() {
		if(OpenID.isAuthenticationResponse()) {
	        // Retrieve the verified id
	        UserInfo userinfo = OpenID.getVerifiedID();
	        if(userinfo == null) {
	              flash.error("不妙，登陆错误。");
	              login();
	        } else {
              String email = userinfo.extensions.get(KEY_EMAIL);
              User user = User.createOrGet(email);
              session.put(CloudPaster.KEY_USER, user.key);
              session.put(KEY_TIMESTAMP, System.currentTimeMillis());
              CloudPaster.my();
	        }
		}
		render();
	}
}
