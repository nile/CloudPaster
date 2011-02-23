package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;
import play.modules.search.Field;
import play.modules.search.Indexed;
import util.CryptoUtil;
import util.PasterUtil;
@Entity
@Indexed
public class Paster extends Model {
	@Field
	public String content;
	public String creator;
	public String skey;
	@Field
	public String title;
	public Type type = Type.Q;
	public Date createDate;
	public Date updateDate;
	public String tagstext;
	
	@OneToMany
	public List<Tag> tags = new ArrayList<Tag>();
	public String src = ModelConstants.PASTER_SRC_WEB;
	public int rating;
	public int useful;
	public int useless;
	public static enum Type{
		Q,A,C
	}
	public static Paster createAndSave(String title,String content,String email,String tags) {
		return createAndSave(title,content, email,tags, null);
	}
	public static Paster createAndSave(String title,String content,String email,String tagstext,String src) {
		Paster paster = new Paster();
		content = PasterUtil.cleanUpAndConvertImages(content, email);
		paster.content = content;
		paster.creator = email;
		paster.createDate = new Date();
		paster.title = title;
		paster.tagstext = tagstext;
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
		
		String[] tagNames = tagstext.trim().split("[ ,;]");
		for (String tag : tagNames) {
			if(StringUtils.isNotEmpty(tag))
				paster.tagWith(tag);
		}
		paster.save();
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
		return Paster.find(order).from(from).fetch(10);
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
		String query ="content:" + StringUtils.join(keywords.split(" +")," AND content:");
		/*Query search = MongoSearch.search(query , Paster.class).page(from, pagesize);
		List<Paster> fetch = search.fetch();*/
		return new QueryResult(null,0);
	}
}
