package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.modules.ebean.Model;

@Entity
public class OpenIdInfo extends Model{
	@ManyToOne
	public User user;
	public String openid;
	public String provider;
}
