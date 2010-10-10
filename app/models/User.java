package models;

import java.util.Date;

import play.modules.mongo.MongoEntity;
import play.modules.mongo.MongoModel;
import util.CryptoUtil;
@MongoEntity("m_user")
public class User extends MongoModel{
	public String email;
	public String key;
	public Date	createDate;
	public User(String email,String key) {
		this.email = email;
		this.key = key;
		this.createDate = new Date();
	}
	public static User getByKey(String key) {
		return User.find("byKey", key).first();
	}
	public static User createOrGet(String email) {
		User user = User.find("byEmail",email).first();
		if(user!=null)
			return user;
		String randomstr = CryptoUtil.randomstr(120);
		while (getByKey(randomstr) != null) {
			randomstr = CryptoUtil.randomstr(120);
		}
		user = new User(email, randomstr);
		user.save();
		return user;
	}
}