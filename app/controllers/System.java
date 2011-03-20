/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.util.List;
import models.ConfigItem;
import play.mvc.Controller;

/**
 *
 * @author nile
 */
public class System extends Controller{
    public static void sysinfo(){
        final Runtime runtime = Runtime.getRuntime();  
        final List<ConfigItem> configs = ConfigItem.all().fetch();
        render(runtime,configs);
    }
}
