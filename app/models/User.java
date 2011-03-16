package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class User extends Model{
	public String name;
	public String realname;
	public String website;
	public String location;
	public Date birthday;
	public String about;
	public String email;
	public Date	createDate;
	
	public static User createOrGet(String email) {
		User user = User.find("byEmail",email).first();
		if(user!=null)
			return user;
		user = new User();
		user.createDate = new Date();
		user.email = email;
		user.save();
		return user;
	}
}
