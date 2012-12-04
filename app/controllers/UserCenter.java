package controllers;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.Subscribe;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;
@With({Deadbolt.class,GlobalUser.class})
public class UserCenter extends Controller{
    @Restrictions(@Restrict("user"))
	static public void save(User user) {
		user.save();
		index(Auth.getLoginUser().id);
	}
    @Restrictions(@Restrict("user"))
	static public void edit() {
		User user = Auth.getLoginUser();
		render(user);
	}
    @Restrictions(@Restrict("user"))
	static public void index(Long id) {
		User user = User.findById(id);
        List<String> subscribes = null;//Subscribe.find("select topic from Subscribe where user = ?", user).findList();
		render(user,subscribes);
	}
    @Restrictions(@Restrict("user"))
        static public void unsubscribe(String topic){
            final User user = Auth.getLoginUser();
            Subscribe.unsubscribe(user,topic);
            //index(user.id);
        }
    @Restrictions(@Restrict("user"))
        static public void subscribe(String topic){
            final User user = Auth.getLoginUser();
            Subscribe.subscribe(user,topic);
            //index(user.id);
        }
}
