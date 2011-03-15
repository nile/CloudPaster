package models;

import java.util.Arrays;
import java.util.List;

import controllers.Auth;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;

public class CPRoleHolder implements RoleHolder {
	public List<? extends Role> getRoles() {
		User loginUser = Auth.getLoginUser();
		if(loginUser!=null) {
			return Arrays.asList(new CPRole("user"));
		}
		return Arrays.asList(new CPRole("guest"));
	}
}