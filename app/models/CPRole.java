package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.modules.ebean.Model;

import models.deadbolt.Role;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
@Entity
@Table(name="cprole")
public class CPRole extends Model implements Role {
	@Id
	public int id;

	private String name;

	public CPRole(String name) {
		this.name = name;
	}

	public String getRoleName() {
		return name;
	}
	public static CPRole createOrGet(String name) {
		Query<CPRole> q = Ebean.createQuery(CPRole.class);
		q.where().eq("name", name);
		CPRole role = q.findUnique();
		if(role==null) {
			role = new CPRole(name);
			role.save();
		}
		return role;
	}
}
