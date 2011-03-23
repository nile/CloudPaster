package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;
import util.CryptoUtil;
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
	@ManyToMany
	public Set<CPRole> roles = new java.util.HashSet<CPRole>();
	public static User createOrGet(String email) {
		User user = User.find("byEmail",email).first();
		if(user!=null)
			return user;
		user = new User();
		if(User.count()==0) {
			user.roles.add(CPRole.createOrGet("admin"));
		}
		user.roles.add(CPRole.createOrGet("user"));
		user.createDate = new Date();
		user.email = email;
		user.save();
		user.name = "用户"+CryptoUtil.longToString(user.id);
		user.save();
		return user;
	}
}
