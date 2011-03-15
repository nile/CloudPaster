package controllers;

import play.mvc.Http.Request;
import play.mvc.Scope.Flash;
import models.CPRoleHolder;
import models.User;
import models.deadbolt.RoleHolder;
import controllers.deadbolt.AbstractDeadboltHandler;

public class CPDeadboltHandler extends AbstractDeadboltHandler {
	public CPDeadboltHandler() {
	}
	public void beforeRoleCheck() {
		System.out.println("beforeRoleCheck");
	}
	@Override
	public RoleHolder getRoleHolder() {
		System.out.println("getRoleHolder");
		return new CPRoleHolder();
	}
	@Override
	public void onAccessFailure(String controllerClassName) {
		Flash.current().put("return.to", Request.current().action);
		Auth.login();
	}
}
