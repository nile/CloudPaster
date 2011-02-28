package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;
import play.modules.search.Field;
import play.modules.search.Indexed;
import play.modules.search.Query;
import play.modules.search.Search;
import util.CryptoUtil;
import util.PasterUtil;
import util.TokenUtil;
@Entity
@Indexed
public class Paster extends Model {
	@Field
	public String content;
	@ManyToOne
	public User creator;
	public String skey;
	@Field(tokenize=true,sortable=true)
	public String title;
	public Type type = Type.Q;
	public Date createDate;
	public Date updateDate;
	public String tagstext;
	public State state = State.NORMAL;
	@OneToOne
	public Paster parent ;
	@OneToMany
	public List<Tag> tags = new ArrayList<Tag>();
	public String src = ModelConstants.PASTER_SRC_WEB;
	public int rating;
	public int useful;
	public int useless;
	@OneToOne
	public User lastUser;
	public static enum Type{
		Q,A,C
	}
	public static enum State{
		NORMAL,HIDDEN
	}
	public static Paster comment(String parentKey,String content,User user) {
		return createAndSave(null,content, user,null, null,parentKey,Type.C);
	}
	public static Paster answer(String parentKey,String content,User user) {
		return createAndSave(null,content, user,null, null,parentKey,Type.A);
	}
	public static Paster create(String title,String content,User user,String tags) {
		return createAndSave(title,content, user,tags, null,null,Type.Q);
	}
	public static Paster createAndSave(String title,String content,User user,String tagstext,String src,String parentKey,Type type) {
		Paster paster = new Paster();
		content = PasterUtil.cleanUpAndConvertImages(content, user.email);
		paster.content = content;
		paster.creator = user;
		paster.createDate = new Date();
		paster.title = title;
		paster.tagstext = tagstext;
		paster.type = type;
		if(StringUtils.isNotEmpty(parentKey)) {
			Paster tmp_parent = paster.getByKey(parentKey);
			paster.parent = tmp_parent;
			paster.parent.updateDate = new Date();
			paster.parent.save();
		}
		String randomstr = CryptoUtil.randomstr(24);
		while(getByKey(randomstr)!=null) {
			randomstr = CryptoUtil.randomstr(24);
		}
		paster.skey = randomstr;
		if(src!=null) {
			paster.src = src;
		}
		paster.rating = 0;
		paster.save();
		if(paster.tagstext!=null) {
			String[] tagNames = tagstext.trim().split("[ ,;]");
			for (String tag : tagNames) {
				if(StringUtils.isNotEmpty(tag))
					paster.tagWith(tag);
			}
			paster.save();
		}
		return paster;
	}
	public Paster tagWith(String tag) {
		this.tags.add(Tag.findOrCreateByName(tag));
		return this;
	}
	public static long countByCreator(String email){
		return Paster.count("byCreator", email);
	}
	public static List<Paster> findByCreator(String email,int from,int pagesize){
		JPAQuery find = Paster.find("creator=? order by createDate desc",email);
		if(from>0)
			find.from(from);
		return find.fetch(pagesize);
	}
	public static List<Paster> findAll(int from ,int pagesize){
		return findAll(from, pagesize,"order by createDate desc");
	}
	public static List<Paster> findMostUseful(int from ,int pagesize){
		return findAll(from, pagesize,"order by useful desc");
	}
	public static List<Paster> findAll(int from ,int pagesize,String order){
		return Paster.find(order).from(from).fetch(pagesize);
	}
	public static List<Paster> findAll(int from ,int pagesize,String query,String order){
		return Paster.find(query +" " + order).from(from).fetch(pagesize);
	}
	public static Paster getByKey(String key) {
		return Paster.find("bySkey", key).first();
	}
	
	public void remove() {
		delete();
	}
	public void useful() {
		useful+=1;
		save();
	}
	public void useless() {
		useless+=1;
		save();
	}
	public static class QueryResult{
		public long count;
		public List<Paster> results;
		public QueryResult(List<Paster> list,long count) {
			this.results = list;
			this.count = count;
		}
	}
	public static QueryResult search(String keywords,int from,int pagesize) {
		String query ="content:" + StringUtils.join(TokenUtil.token(keywords)," AND content:");
		Query q = Search.search(query, Paster.class);
		q.orderBy("title")
		    .page(from*pagesize,pagesize)
		    .reverse();
		List<Paster> fetch = q.fetch();
		return new QueryResult(fetch,q.count());
	}
	public List<Paster> getAnswers() {
		return Paster.find("state = ? and parent.skey = ? and type=?", State.NORMAL,this.skey,Type.A).fetch();
	}
	public List<Paster> getComments() {
		return Paster.find("state = ? and parent.skey = ? and type=?", State.NORMAL, this.skey,Type.C).fetch();
	}
	public void hide() {
		this.state = State.HIDDEN;
		save();
	}
}
