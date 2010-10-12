package models;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.modules.mongo.MongoCursor;
import play.modules.mongo.MongoEntity;
import play.modules.mongo.MongoModel;
import play.modules.mongosearch.Field;
import play.modules.mongosearch.Indexed;
import play.modules.mongosearch.MongoSearch;
import play.modules.mongosearch.MongoSearch.Query;
import util.CryptoUtil;
import util.WikiUtil;
@MongoEntity("m_paster")
@Indexed
public class Paster extends MongoModel {
	@Field(tokenize=true,stored=false)
	public String content;
	public String contentAsHtml;
	public String creator;
	public String key;
	public Date createDate;
	public int rating;
	
	public static Paster  createAndSave(String content,String email) {
		Paster paster = new Paster();
		paster.content = content;
		paster.creator = email;
		paster.createDate = new Date();
		try {
			paster.contentAsHtml = WikiUtil.parse(content);
		} catch (UnsupportedEncodingException e) {
			paster.contentAsHtml = content;
		}
		String randomstr = CryptoUtil.randomstr(24);
		while(getByKey(randomstr)!=null) {
			randomstr = CryptoUtil.randomstr(24);
		}
		paster.key = randomstr;
		paster.rating = 0;
		paster.save();
		MongoSearch.index(paster);
		return paster;
	}
	public static long countByCreator(String email){
		return Paster.count("byCreator", email);
	}
	public static List<Paster> findByCreator(String email,int from,int pagesize){
		MongoCursor find = Paster.find("byCreator",email).order("by-CreateDate");
		if(from>0)
			find.from(from);
		return find.fetch(pagesize);
	}
	public static List<Paster> findAll(int from ,int pagesize){
		return Paster.find().from(from).order("by-CreateDate").fetch(10);
	}
	public static Paster getByKey(String key) {
		return Paster.find("byKey", key).first();
	}
	
	public void remove() {
		MongoSearch.unIndex(this);
		delete();
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
		Query search = MongoSearch.search(query , Paster.class).page(from, pagesize);
		List<Paster> fetch = search.fetch();
		return new QueryResult(fetch,search.count());
	}
}
