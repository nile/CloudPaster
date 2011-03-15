package controllers;

import java.util.List;

import models.Paster;
import models.Paster.QueryResult;
import models.Paster.Type;
import models.User;
import net.sf.oval.constraint.NotEmpty;
import notifiers.Notifier;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.cache.CacheFor;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
@With(Deadbolt.class)
public class CloudPaster extends Controller {
	
	@Before
	static void startTimer() {
		User user = Auth.getLoginUser();
		renderArgs.put("user", user);
	}
	@After
	static void after() {
		session.put(Auth.KEY_TIMESTAMP, System.currentTimeMillis());
	}
	
	/**
	 * 最近活跃
	 */
	static public  void activity() {
		List<Paster> pasters = Paster.find("type=? order by created desc,updated desc " ,Type.Q).fetch(10);
		render(pasters);
	}
	/**
	 * 问题
	 */
	static public  void questions(int from) {
		long count = Paster.count("type=?",Type.Q);
		List<Paster> pasters = Paster.find("type = ? order by created desc", Type.Q).from(from).fetch(10);
		render(pasters,from,count);
	}
	/**
	 * 等待回答
	 */
	static public  void unanswered(int from) {
		long count = Paster.count("type=? and answerCount = 0",Type.Q);
		List<Paster> pasters = Paster.find("type=? and answerCount = 0 order by created desc", Type.Q).from(from).fetch(10);
		render(pasters,from,count);
	}
	/**
	 * 标签和分类
	 */
	static public void tags() {
		render();
	}
	static public void tag(String name) {
		render();
	}
	/**
	 * 用户
	 */
	static public void users() {
		render();
	}
	static public void hide(long id) {
		Paster paster = Paster.findById(id);
		paster.hide();
		if(paster.type == Type.A || paster.type == Type.C) {
			while(paster.parent!=null) {
				paster = paster.parent;
			}
		}
		view(paster.id);
	}
	static public void comment(long id,long answerId,String content) {
		Paster paster = Paster.findById(id);
		if(StringUtils.isNotEmpty(params.get("docommentadd"))) {
			Paster.comment(answerId>0?answerId:id, content, Auth.getLoginUser());
			view(id);
		}
		if(StringUtils.isNotBlank(params.get("docancel"))) {
			view(id);
		}
		String state = "comment";
		if(answerId>0) {
			state = "answer-comment";
		}
		render("@view",paster,state,answerId);
	}
	static public void answer(long id,String content) {
		Paster paster = Paster.findById(id);
		if(StringUtils.isNotEmpty(params.get("doansweradd"))) {
			Paster.answer(id, content, Auth.getLoginUser());
			view(id);
		}
		if(StringUtils.isNotBlank(params.get("docancel"))) {
			view(id);
		}
		String state = "answer";
		render("@view",paster,state);
	}
	/**
	 * 提问
	 */
	@Restrictions(@Restrict("user"))
	static public void ask(Long id,@NotEmpty String title,
			@NotEmpty String content,String tagstext) {
		
		if(StringUtils.isNotEmpty(params.get("dosave"))&& id>0) {
			Paster paster = Paster.update(id, title, content, Auth.getLoginUser(), tagstext);
			view(paster.id);
		}
		
		if(StringUtils.isNotEmpty(params.get("doprequery"))) {
			params.flash();
			QueryResult search = Paster.search(title, 0, 5);
			List<Paster> recommendPosts = search.results;
			if(search.count>0) {
				render(recommendPosts);
			}else {
				boolean newask = true;
				render(newask);
			}
		}
		if(StringUtils.isNotEmpty(params.get("newask"))) {
			params.flash();
			Boolean newask = true;
			render(newask);
		}
		if(StringUtils.isNotEmpty(params.get("doaddask"))) {
			params.flash();
			Paster paster = Paster.create(title,content, Auth.getLoginUser(),tagstext);
			view(paster.id);
		}
		boolean start = true;
		render(start);
	}
	public static void edit(long id){
		Paster paster = Paster.findById(id);
		Boolean edit=true;
		render("@ask",paster,edit);
	}
	public static void index() {
		//if(session.contains(KEY_USER)) {			
		//	render();
		//} else {
			//intro();
		//}
		activity();
	}
	//@CacheFor("15s")
	public static void load(int from) {
		List<Paster> pasters = Paster.findAll(from, 10);
		long count = Paster.count();
		request.format="json";
		render(pasters, count, from);
	}
	public static void loadmy(int from) {
		User user = Auth.getLoginUser();
		List<Paster> pasters = Paster.findByCreator(user.email, from, 10);
		long count = Paster.countByCreator(user.email);
		request.format="json";
		render("@load",pasters, count, from);
	}
	@CacheFor("15s")
	public static void loadMostUseful() {
		List<Paster> pasters = Paster.findMostUseful(0, 10);
		int from = 0;
		long count = 10;
		request.format="json";
		render("@load",pasters, count , from );
	}
	public static void intro() {
		render();
	}
	public static void prepaste() {
		render();
	}
	
	public static void my() {
		render();
	}

	public static void view(long id) {
		Paster paster = Paster.findById(id);
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
	
	public static void delete(long id) {
		User user = Auth.getLoginUser();
		Paster obj = Paster.findById(id);
		if (obj != null && obj.creator.equals(user.email)) {
			obj.remove();
		}
		my();
	}

	public static void paste(@Required(message="title required") String title,
			@Required(message = "content is required.") String content,
			String tagstext) {
		if (!Validation.hasErrors()) {
			User user = Auth.getLoginUser();
			Paster paster = Paster.create(title,content, user,tagstext);
			if(Boolean.valueOf(Play.configuration.getProperty("notifier.enabled","false"))) {
				Notifier.paste(user.email, paster);
			}
			success(paster);
		} else {
			fail("ERR-001", "内容字段不能为空");
		}
	}

	static void fail(String code,String message) {
		request.format="json";
		response.contentType="text/json";
		render("@fail",code,message);
	}
	static void success(Paster paster) {
		request.format="json";
		response.contentType="text/json";
		render("@success",paster);
	}
	public static void voteup(long id) {
		User user = Auth.getLoginUser();
		Paster paster = Paster.findById(id);
		Event event = Event.find("action=? and user=? and target=?", Action.Voteup,user,paster).first();
		if(event!=null) {
			fail("vote-dumplicate","只能投票一次");
		}
		Event newevent = new Event();
		newevent.action = Action.Voteup;
		newevent.user = user;
		newevent.target = paster;
		newevent.save();
		paster.voteup();
		success(paster);
	}
	public static void votedown(long id) {
		User user = Auth.getLoginUser();
		Paster paster = Paster.findById(id);
		Event event = Event.find("action=? and user=? and target=?", Action.Votedown,user,paster).first();
		if(event!=null) {
			fail("vote-dumplicate","只能投票一次");
		}
		Event newevent = new Event();
		newevent.action = Action.Voteup;
		newevent.user = user;
		newevent.target = paster;
		newevent.save();
		paster.votedown();
		success(paster);
	}
}
