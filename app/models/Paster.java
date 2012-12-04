package models;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.avaje.ebean.Ebean;

import play.modules.search.Field;
import play.modules.ebean.EbeanSupport;
import play.modules.ebean.Model;
import play.modules.search.Indexed;
import play.modules.search.Query;
import play.modules.search.Search;
import util.PasterConverter;
import util.TokenUtil;
import ys.wikiparser.WikiParser;
@Entity
@Table(name="paster")
@Indexed(converters = {PasterConverter.class})
public class Paster extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4480896812936822950L;
	@Field
	public String content;
	public String wiki;
	@Field(tokenize=true)
	public String title;
	@OneToOne
	public Paster parent ;
	@OneToOne
	public Paster best;
    @OneToOne
	public User creator;
	public Date created;
	public Type type = Type.Q;
	public String tagstext;
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="paster_tag",
	 	joinColumns= { @JoinColumn(name="paster_id", referencedColumnName="id")},
		inverseJoinColumns = {@JoinColumn(name="tags_id",referencedColumnName="id",table="tag")}
		)
	public Set<Tag> tags;
	@OneToOne
	@JoinColumn(name="lastUser_id")
	public User lastUser;
	public Date updated;
	public State state = State.NORMAL;
	public int voteup;
	public int votedown;
	@Column(name="viewCount")
	public int viewCount;
	@Column(name="answerCount")
	public int answerCount;
	@Column(name="commentCount")
	public int commentCount;
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="lastAnswerUser_id")
	public User lastAnswerUser;
	@OneToOne
	@JoinColumn(name="lastAnswer_id")
	public Paster lastAnswer;
	@Column(name="lastAnswered")
	public Date lastAnswered;
	
	public static enum Type{
		Q,A,C
	}
	public static enum State{
		NORMAL,HIDDEN
	}
	public static Paster comment(long parentId,String content,User user) {
		return createAndSave(null,content, user,null, -1,parentId,Type.C,false);
	}
	public static Paster answer(long parentId,String content,User user) {
		return createAndSave(null,content, user,null, -1,parentId,Type.A,true);
	}
	public static Paster create(String title,String content,User user,String tags) {
		return createAndSave(title,content, user,tags, -1,-1,Type.Q,true);
	}
	public static Paster update(long id ,String title,String content,User user,String tags) {
		return createAndSave(title,content, user,tags, id,-1,null,true);
	}
	public static Paster createAndSave(String title,String content,User user,String tagstext,long id,long parentId,Type type,boolean wiki) {
		Paster paster = null;
		if(id > 0) {
			paster = Paster.findById(id);
			paster.lastUser = user;
			paster.updated = new Date();
		} else {
			paster = new Paster();
			paster.created= new Date();
			paster.type = type;
			paster.creator = user;
		}
		paster.wiki = content;
		if(wiki)
			paster.content = WikiParser.renderXHTML(paster.wiki);
		else
			paster.content = Jsoup.clean(paster.wiki,Whitelist.none());
		//PasterUtil.cleanUpAndConvertImages(content, user.email);
		//paster.content = content;
		paster.title = title;
		paster.tagstext = tagstext;
        paster.tags.clear();
		if(paster.tagstext!=null) {
			String[] tagNames = tagstext.trim().split("[ ,;]");
			for (String tag : tagNames) {
				if(StringUtils.isNotEmpty(tag)) {
					paster.tagWith(tag);
				}
			}
		}
		if(parentId>0) {
			Paster tmp_parent = Paster.findById(parentId);
			paster.parent = tmp_parent;
			if(type== Type.C)
				paster.parent.commentCount++;
			if(type == Type.A) {
				paster.parent.answerCount++;
				paster.parent.lastAnswerUser = user;
				paster.parent.lastAnswer = paster;
				paster.parent.lastAnswered = new Date();
			}
		}
		paster.save();
        if(parentId >0)
            paster.parent.save();
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
		com.avaje.ebean.Query<Paster> find = Paster.find("creator=? order by createDate desc",email);
		if(from>0)
			find.setFirstRow(from);
		find.setMaxRows(from+pagesize);
		return find.findList();
	}
	public static List<Paster> findAll(int from ,int pagesize){
		return findAll(from, pagesize,"order by createDate desc");
	}
	public static List<Paster> findMostUseful(int from ,int pagesize){
		return findAll(from, pagesize,"order by useful desc");
	}
	public static List<Paster> findAll(int from ,int pagesize,String order){
		com.avaje.ebean.Query<Paster> query = Paster.find(order);
		return query.setFirstRow(from).setMaxRows(pagesize+from).findList();
	}
	public static List<Paster> findAll(int from ,int pagesize,String query,String order){
		com.avaje.ebean.Query<Paster> equery = Paster.find(query +" " + order);
		return equery.setFirstRow(from).setMaxRows(from+ pagesize).findList();
	}
	
	public void remove() {
		delete();
	}
        public boolean hadVoteup(User user){
        	if(user == null) return false;
	    return Event.count("action=? and user_id=? and target_id=?", Action.Voteup.ordinal(), user.id, this.id)>0;
	}
    public boolean hadVotedown(User user){
    	if(user == null) return false;
        return Event.count("action=? and user_id=? and target_id=?", Action.Votedown.ordinal(), user.id, this.id)>0;
    }
	public void voteup(boolean up) {
	    voteup+=(up?1:-1);		
	    save();
	}
	public void votedown(boolean up) {
	    votedown+=(up?1:-1);
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

	public static QueryResult search(String keywords, int from, int pagesize) {
		String query = "content:(" + StringUtils.join(TokenUtil.token(keywords), " AND ")+")";
		query += " OR title:(" + StringUtils.join(TokenUtil.token(keywords), " AND ")+")";
		Query q = Search.search(query, Paster.class);
		q.orderBy("title").page(from, pagesize).reverse();
		List<Paster> fetch = null;//q.fetch();
		return new QueryResult(fetch, q.count());
	}
	public List<Paster> getAnswers() {
		com.avaje.ebean.Query<Paster> query = Paster.find("state = ? and parent.id = ? and type=?", State.NORMAL,this.id,Type.A);
		return query.findList();
	}
	public List<Paster> getComments() {
		com.avaje.ebean.Query<Paster> query = Paster.find("state = ? and parent.id= ? and type=?", State.NORMAL, this.id,Type.C);
		return query.findList();
	}
	public void hide() {
		this.state = State.HIDDEN;
		if(parent!=null && this.type == Type.A) {
			parent.answerCount--;
			parent.save();
		}
		if(parent!=null && this.type == Type.C) {
			parent.commentCount--;
			parent.save();
		}
		save();
	}
}
