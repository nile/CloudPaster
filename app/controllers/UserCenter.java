package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;

public class UserCenter extends Controller{
	@Before
	static void startTimer() {
		User user = Auth.getLoginUser();
		renderArgs.put("user", user);
	}
	static public void save(User user) {
		user.save();
		index(Auth.getLoginUser().id);
	}
	static public void edit() {
		User user = Auth.getLoginUser();
		render(user);
	}
	static public void index(Long id) {
		User user = Auth.getLoginUser();
		render(user);
	}
}
