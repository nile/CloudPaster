package models;

import java.util.Date;

import play.modules.mongo.MongoEntity;
import play.modules.mongo.MongoModel;

@MongoEntity("m_log")
public class Log extends MongoModel {
	private static final String LOGIN="login", LOGOUT="logout", RATINGUP="ratingup", RATINGDOWN="ratingdown", PASTE="paste",DELETE="delete",USEFUL="useful",USELESS="useless";

	public Date occurtime;
	public String user;
	public String action;
	public String target;
	
	public static void login(String user) {
		log(user,LOGIN,"");
	}
	public static void logout(String user) {
		log(user,LOGOUT,"");
	}
	public static void paste(String user,String key) {
		log(user,PASTE,key);
	}
	public static void useful(String user,String key) {
		log(user,USEFUL,key);
	}
	public static void useless(String user,String key) {
		log(user,USELESS,key);
	}
	public static void ratingup(String user,String key) {
		log(user,RATINGUP,key);
	}
	public static void ratingdown(String user,String key) {
		log(user,RATINGDOWN,key);
	}
	public static void delete(String user,String key) {
		log(user,DELETE,key);
	}
	private static void log(String user,String action,String target) {
		Log log = new Log();
		log.occurtime = new Date();
		log.user = user;
		log.action = action;
		log.target = target;
		log.save();
	}
}
