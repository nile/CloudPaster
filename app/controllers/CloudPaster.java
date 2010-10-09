package controllers;

import java.util.List;

import models.Paster;
import models.User;
import models.Paster.QueryResult;
import notifiers.Notifier;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

public class CloudPaster extends Controller {
	private static final long MINS_15 = 15*60*1000L;
	static final String KEY_USER = "user.key";
	@Before
	static void startTimer() {
	}
	@After
	static void after() {
		session.put("timestamp", System.currentTimeMillis());
	}
	
	@Before(unless={"login","googlelogin","yahoologin","index","view","search","intro"})
	static void checkAuthenticated() {
	    if(!session.contains(KEY_USER)) {
	        Auth.login();
	    }
	    if(!session.contains("timestamp")) {
	    	int last = Integer.parseInt(session.get("timestamp"));
	    	if(System.currentTimeMillis() - last > MINS_15 ) {
	    		Auth.login();
	    	}
	    }
	}
	
	public static void index(int from) {
		if(session.contains(KEY_USER)) {
			List<Paster> pasters = Paster.findAll(from, 10);
			long count = Paster.count();
			render(pasters, count, from);
		} else {
			intro();
		}
	}
	public static void intro() {
		render();
	}
	public static void prepaste() {
		render();
	}

	public static void my(int from) {
		String userkey = session.get(KEY_USER);
		if (userkey != null) {
			User user = User.getByKey(userkey);
			List<Paster> pasters = Paster.findByCreator(user.email, from, 10);
			long count = Paster.countByCreator(user.email);
			render(pasters, count, from);
		} else {
			index(0);
		}
	}

	public static void view(String key) {
		Paster paster = Paster.getByKey(key); 
		render(paster);
	}
	
	public static void search(String keywords,int from) {
		if(StringUtils.isNotEmpty(keywords)) {
			QueryResult search = Paster.search(keywords,from,10);
			long count = search.count;
			List<Paster> pasters = search.results;
			render(pasters,count,from,keywords);
		}else {
			flash.error("请输入要查询的内容。");
			render();
		}
	}
	
	public static void delete(String key) {
		String userkey = session.get(KEY_USER);
		if(userkey !=null) {
			User user = User.getByKey(userkey);
			Paster obj = Paster.getByKey(key);
			if (obj != null && obj.creator.equals(user.email)) {
				obj.remove();
			}
		}
		my(0);
	}

	public static void paste(@Required(message = "content is required.") String content) {
		if (!Validation.hasErrors()) {
			String userkey = session.get(KEY_USER);
			User user = User.getByKey(userkey);
			Paster paster = Paster.createAndSave(content, user.email);
			if(Boolean.valueOf(Play.configuration.getProperty("notifier.enabled","false"))) {
				Notifier.paste(user.email, paster);
			}
			success(paster.key);
		} else {
			params.flash();
			Validation.keep();
			prepaste();
		}
	}

	public static void success(String key) {
		Paster paster = Paster.getByKey(key);
		render(paster);
	}
}
