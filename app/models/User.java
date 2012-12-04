package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.avaje.ebean.Query;

import play.modules.ebean.EbeanSupport;
import play.modules.ebean.Model;

import util.CryptoUtil;
@Entity
@Table(name="user")
public class User extends Model{
	public String name;
	public String realname;
	public String website;
	public String location;
	public Date birthday;
	public String about;
	public String email;
	@Column(name="createDate")
	public Date	createDate;
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="user_cprole")
	public Set<CPRole> roles = new java.util.HashSet<CPRole>();
	public static User createOrGet(String email) {
		Query<User> find = User.find("email = ?",email);
		User user = find.findUnique();
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
