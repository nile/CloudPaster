package models;

import models.deadbolt.Role;

public class CPRole implements Role {

	private String name;

	public CPRole(String name) {
		this.name = name;
	}

	public String getRoleName() {
		return name;
	}

}
