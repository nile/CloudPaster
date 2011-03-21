package controllers;

import models.User;
import play.mvc.Http.Request;
import play.mvc.Scope.Flash;
import models.CPRoleHolder;
import models.deadbolt.RoleHolder;
import controllers.deadbolt.AbstractDeadboltHandler;

public class CPDeadboltHandler extends AbstractDeadboltHandler {
	public CPDeadboltHandler() {
	}
	public void beforeRoleCheck() {
	}
	@Override
	public RoleHolder getRoleHolder() {
		return new CPRoleHolder();
	}
	@Override
	public void onAccessFailure(String controllerClassName) {
        final User loginUser = Auth.getLoginUser();
        if(loginUser!=null){
            Flash.current().error("权限不足！");
        }
		Flash.current().put("return.to", Request.current().url);
		Auth.login();
	}
}
