/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.util.List;
import models.ConfigItem;
import play.Play;
import play.db.jpa.JPABase;
import play.libs.Mail;
import play.mvc.Controller;

/**
 *
 * @author nile
 */
public class Manager extends Controller{
    public static void sysinfo(){
        final Runtime runtime = Runtime.getRuntime();  
        final List<ConfigItem> configs = ConfigItem.all().fetch();
        render(runtime,configs);
    }
    public static void delconfig(long id) {
    	ConfigItem item = ConfigItem.findById(id);
    	item.delete();
    	sysinfo();
    }
    public static void addconfig(ConfigItem item) {
    	ConfigItem.createOrSave(item);
    	sysinfo();
    }
    public static void apply() {
    	final List<ConfigItem> configs = ConfigItem.all().fetch();
    	for (ConfigItem item : configs) {
    		Play.configuration.put(item.name, item.val);
		}
    	Mail.session = null;
    	flash.success("配置成功应用。");
    	sysinfo();
    }
}
