package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import models.deadbolt.Role;
import play.db.jpa.Model;
@Entity
@Table(name="cprole")
public class CPRole extends Model implements Role {
	private String name;

	public CPRole(String name) {
		this.name = name;
	}

	public String getRoleName() {
		return name;
	}
	public static CPRole createOrGet(String name) {
		CPRole role = CPRole.find("byName", name).first();
		if(role==null) {
			role = new CPRole(name);
			role.save();
		}
		return role;
	}
}
