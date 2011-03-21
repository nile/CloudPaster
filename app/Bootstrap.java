import java.util.List;

import models.CPRole;
import models.ConfigItem;
import play.Play;
import play.data.parsing.DataParser;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import util.RobotMessager;
import util.SimpleJsonParser;

@OnApplicationStart 
public class Bootstrap extends Job {

	public void doJob() {
		DataParser.parsers.put("application/json", new SimpleJsonParser());
         if(Boolean.valueOf(Play.configuration.getProperty("robot.enabled", "false"))) {
        	 RobotMessager.init(Play.configuration.getProperty("robot.msnaccount"), Play.configuration.getProperty("robot.password"));
        	 RobotMessager.login();
         }
         //应用系统配置
        final List<ConfigItem> configs = ConfigItem.all().fetch();
     	for (ConfigItem item : configs) {
     		Play.configuration.put(item.name, item.val);
 		}
     	//创建角色
     	CPRole.createOrGet("user");
     	CPRole.createOrGet("admin");
     	CPRole.createOrGet("guest");
	}
}
