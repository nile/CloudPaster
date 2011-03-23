package controllers;

import java.util.List;
import models.Subscribe;
import models.User;
import play.db.jpa.GenericModel.JPAQuery;
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
                List<String> subscribes = Subscribe.find("select topic from Subscribe where user = ?", user).fetch();
		render(user,subscribes);
	}
        static public void unsubscribe(String topic){
            final User user = Auth.getLoginUser();
            Subscribe.unsubscribe(user,topic);
            //index(user.id);
        }
        static public void subscribe(String topic){
            final User user = Auth.getLoginUser();
            Subscribe.subscribe(user,topic);
            //index(user.id);
        }
}
