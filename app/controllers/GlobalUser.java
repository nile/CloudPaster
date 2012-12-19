/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import models.User;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

/**
 *
 * @author nile
 */
public class GlobalUser extends Controller {

    @Before
    static void startTimer() {
        User user = Auth.getLoginUser();
    	renderArgs.put("user", user);
    }

    @After
    static void after() {
        session.put(Auth.KEY_TIMESTAMP, System.currentTimeMillis());
    }
}
