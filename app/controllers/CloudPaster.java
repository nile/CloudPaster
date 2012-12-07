package controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Action;
import models.Event;
import models.FavoriteTag;
import models.Paster;
import models.Paster.QueryResult;
import models.Paster.Type;
import models.Subscribe;
import models.Tag;
import models.User;
import net.sf.oval.constraint.NotEmpty;
import notifiers.Notifier;

import org.apache.commons.lang.StringUtils;

import play.modules.ebean.EbeanSupport;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import controllers.vm.Result;

@With({Deadbolt.class,GlobalUser.class})
public class CloudPaster extends Controller {
	final static int PAGE_SIZE=30;
        @Before
        static public void prepare(){
            if(Auth.getLoginUser()!=null){
                Query<Tag> q = Tag.find("exists (select * from favorite_tag where user_id = ? and tag_id = id)", Auth.getLoginUser().id);
				List<Tag> favoriteTags = q.findList();
                renderArgs.put("favoriteTags", favoriteTags);
            }
        }
    /**
     * 最近活跃
     */
    static public void activity() {
        Query<Paster> query = Paster.find("type=? order by created desc,updated desc ", Type.Q);
		List<Paster> pasters = query.setMaxRows(PAGE_SIZE).findList();
        render(pasters);
    }

    /**
     * 问题
     */
    static public void questions(int from) {
        long count = Paster.count("type=?", Type.Q);
        Query<Paster> query = Paster.find("type = ? order by created desc", Type.Q);
        query
        .select("id, title, created ,type, updated, state, voteup, votedown, viewCount, answerCount, commentCount, lastAnswered ")
        .fetch("tags",new FetchConfig().query())
        .fetch("lastUser",new FetchConfig().query())
        .fetch("lastAnswerUser",new FetchConfig().query())
        .fetch("creator",new FetchConfig().query())
        ;
		List<Paster> pasters = query.setFirstRow(from).setMaxRows(PAGE_SIZE).findList();
	long pagesize = PAGE_SIZE;
	Query<Tag> q = Tag.find(
				    "select new map(t.name as name, count(p.id) as count) from Paster p join p.tags as t group by t.name order by count(p.id) desc"
				    );
	List<Tag> clouds = null;//q.findList();
	render(pasters, from, count, pagesize, clouds);
    }

    /**
     * 等待回答
     */
    static public void unanswered(int from) {
        long count = Paster.count("type=? and answerCount = 0", Type.Q);
        Query<Paster> query = Paster.find("type=? and answerCount = 0 order by created desc", Type.Q);
		List<Paster> pasters = query.setFirstRow(from).setMaxRows(PAGE_SIZE).findList();
		long pagesize = PAGE_SIZE;
        render(pasters, from, count, pagesize);
    }

    /**
     * 标签和分类
     */
    static public void tags() {
        /*List<Map> clouds = null ;*//*Tag.find(
            "select new map(t.name as name, count(p.id) as count) from Paster p join p.tags as t group by t.name order by count(p.id) desc"
        ).findList();*/
        List<SqlRow> clouds = Ebean.createSqlQuery("SELECT t.NAME AS NAME, COUNT(p.paster_id) AS COUNT FROM tag t JOIN paster_tag p ON p.tags_id = t.id GROUP BY t.NAME ORDER BY COUNT DESC").findList();
        render(clouds);
    }

    static public void tag(String name, int from) {
        long count = 30;//Paster.count(" paster_tag pt t where t.name = ?", name);
        Query<Paster> query = Paster.find("select distinct p from Paster p join p.tags as t where t.name = ?", name);
        Query<Paster> q = Ebean.find(Paster.class).where("exists(select * from paster_tag join tag t on tags_id = t.id where paster_id = id and t.name like ? ) ").setParameter(1, name);
        count = q.getTotalHits();
		List<Paster> pasters = q.setFirstRow(from).setMaxRows(PAGE_SIZE).findList();
		long pagesize = PAGE_SIZE;
        render(pasters, from, count, pagesize, name);
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
        if (paster.type == Type.A || paster.type == Type.C) {
            while (paster.parent != null) {
                paster = paster.parent;
            }
        }
        view(paster.id);
    }

    @Restrictions(@Restrict("user"))
    static public void comment(long id, long aid, String content) {
        Paster paster = Paster.findById(id);
        if (StringUtils.isNotEmpty(params.get("docommentadd"))) {
            Paster.comment(aid > 0 ? aid : id, content, Auth.getLoginUser());
            view(id);
        }
        if (StringUtils.isNotBlank(params.get("docancel"))) {
            view(id);
        }
        String state = "comment";
        if (aid > 0) {
            state = "answer-comment";
        }
        render("@view", paster, state, aid);
    }

    @Restrictions(@Restrict("user"))
    static public void answer(long id, long aid, String content) {
        if (StringUtils.isNotEmpty(params.get("doansweradd"))) {
            Paster answer = Paster.answer(id, content, Auth.getLoginUser());
            if(Subscribe.count("user_id = ? and topic = ?", Auth.getLoginUser().id,Subscribe.TOPIC_ANSWER_FOR_ME)>0)
            	Notifier.anwser(answer.parent, answer);
            view(id);
        }
        if (StringUtils.isNotEmpty(params.get("doupdateanswer"))) {
            Paster answer = Paster.findById(aid);
            if (answer.creator.id == Auth.getLoginUser().id) {
                Paster.update(aid, answer.title, content, Auth.getLoginUser(), null);
            }
            view(id);
        }
        if (StringUtils.isNotBlank(params.get("docancel"))) {
            view(id);
        }
        Paster paster = Paster.findById(id);
        String state = "answer";
        render("@view", paster, state);
    }

    /**
     * 提问
     */
    @Restrictions(@Restrict("user"))
    static public void ask(Long id, @NotEmpty String title,
            @NotEmpty String content, String tagstext) {
        String state = "";
        if (StringUtils.isNotEmpty(params.get("doupdatequestion")) && id > 0) {
            Paster tmp = Paster.findById(id);
            if (Auth.getLoginUser().id == tmp.creator.id) {
                Paster paster = Paster.update(id, title, content, Auth.getLoginUser(), tagstext);
                view(paster.id);
            } else {
                flash.error("你不能修改别人的问题");
            }
        }
        if (StringUtils.isNotEmpty(params.get("doprequery"))) {
            params.flash();
            QueryResult search = Paster.search(title, 0, 5);
            List<Paster> recommendPosts = search.results;
            if (search.count > 0) {
                render(recommendPosts);
            } else {
                state = "newask";
                render(state);
            }
        }
        if (StringUtils.isNotEmpty(params.get("newask"))) {
            params.flash();
            state = "newask";
            render(state);
        }
        if (StringUtils.isNotEmpty(params.get("doaddask"))) {
            params.flash();
            Paster paster = Paster.create(title, content, Auth.getLoginUser(), tagstext);
            Query<User> q = User.find("exists (select * from subscribe s where s.user_id = id ) ");
			List<User> subscribeUsers = q.select("email,name").findList();
            Notifier.newquestion(subscribeUsers, paster);
            view(paster.id);
        }
        state = "start";
        render(state);
    }
    @Restrictions(@Restrict("user"))
    public static void edit(long id) {
        Paster paster = Paster.findById(id);
        if(paster.creator.id != Auth.getLoginUser().id){
	    jsonresult("failed","permission-limited", "你不能修改别人的问题",0);
	    return;
        }
        String state = "answer-edit";
        if (paster.type == Type.A) {
            paster = paster.parent;
            render("@view", paster, state, id);
        }
        state = "question-edit";
        render("@view", paster, state);
    }

    public static void view(long id) {
        Paster paster = Paster.findById(id);
        paster.viewCount ++;
        paster.save();
        Set<Tag> tags = paster.tags;
        render(paster,tags);
    }

    public static void search(String keywords, int from) {
        if (StringUtils.isNotEmpty(keywords)) {
            QueryResult search = Paster.search(keywords, from, PAGE_SIZE);
            long count = search.count;
            List<Paster> pasters = search.results;
			long pagesize = PAGE_SIZE;
            render(pasters, count, from, pagesize, keywords);
        } else {
            flash.error("请输入要查询的内容。");
            render();
        }
    }

    static void jsonresult(String state, String code, String msg,Object data){
        renderJSON(Result.one(state, code, msg, data));
    }

    public static void voteup(long id) {
        User user = Auth.getLoginUser();
	if(user == null){
	    jsonresult("failed","need-login","请登录",0);
	}
        Paster paster = Paster.findById(id);
        Query<Event> q = Event.find("action in(?,?) and user_id =? and target_id=?", Action.Votedown,Action.Voteup, user.id, paster.id);
		Event event = q.findUnique();
        if (event != null) {
	    event.delete();
	    if(event.action == Action.Votedown){
		paster.votedown(false);
	    }else{
		paster.voteup(false);
		jsonresult("ok","vote-success","已经取消",paster.voteup-paster.votedown);
	    }
        }
	Event newevent = new Event();
	newevent.action = Action.Voteup;
	newevent.user = user;
	newevent.target = paster;
	newevent.save();
	paster.voteup(true);
	jsonresult("ok","vote-success","顶上了",paster.voteup-paster.votedown);
    }

    public static void votedown(long id) {
        User user = Auth.getLoginUser();
	if(user == null){
	    jsonresult("failed","need-login","请登录",0);
	}
        Paster paster = Paster.findById(id);
        Query<Event> q = Event.find("action in(?,?) and user_id=? and target_id=?", Action.Votedown,Action.Voteup, user.id, paster.id);
		Event event = q.findUnique();
        if (event != null) {
	    event.delete();
	    if(event.action == Action.Voteup){
		paster.voteup(false);
	    }else{
		paster.votedown(false);
		jsonresult("ok","vote-success","已经取消",paster.voteup-paster.votedown);
	    }
        }
	Event newevent = new Event();
	newevent.action = Action.Votedown;
	newevent.user = user;
	newevent.target = paster;
	newevent.save();
	paster.votedown(true);
	jsonresult("ok","vote-success","踩中了",paster.voteup-paster.votedown);
    }
    public static void removeFavoriteTag(String tagName){
        User user =  Auth.getLoginUser();
        if(user == null){
            jsonresult("failed","need-login","请登录",0);
            return;
        }
        Tag tag = Tag.findByName(tagName);
        if(tag == null){
            jsonresult("failed","tag-not-exists","指定的分类不存在",0);
            return;
        }
        FavoriteTag ft = new FavoriteTag();
        if(FavoriteTag.count("user_id = ? and tag_id = ?",user.id,tag.id)>0){
            FavoriteTag.delete("user_id=? and tag_id = ?", user.id, tag.id);
            jsonresult("ok","tag-unfocused","已经取消关注",tag);
            return;
        }
        jsonresult("faild","tag-not-focused","没有关注",tag);
    }
    public static void addFavoriteTag(String tagName){
	User user =  Auth.getLoginUser();
	if(user == null){
	    jsonresult("failed","need-login","请登录",0);
	    return;
	}
	Tag tag = Tag.findByName(tagName);
	if(tag == null){
	    jsonresult("failed","tag-not-exists","指定的分类不存在",0);
	    return;
	}
	FavoriteTag ft = new FavoriteTag();
	if(FavoriteTag.count("user_id = ? and tag_id = ?",Auth.getLoginUser().id,tag.id)>0){
	    jsonresult("failed","tag-had-focused","已经关注了",0);
	    return;
	}
	
	ft.user = user;
	ft.tag = tag;
	ft.save();
	jsonresult("ok","tag-followed","关注成功",tag);
    }
    public static void tagInfo(String name){
        Tag tag = Tag.findByName(name);
        render(tag);
    }
}
