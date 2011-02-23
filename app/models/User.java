package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
import util.CryptoUtil;
@Entity
public class User extends Model{
	public String email;
	public String skey;
	public Date	createDate;
	
	public User(String email,String key) {
		this.email = email;
		this.skey = key;
		this.createDate = new Date();
	}
	public static User getByKey(String key) {
		return User.find("bySkey", key).first();
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
