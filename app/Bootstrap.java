import play.Play;
import play.data.parsing.DataParser;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.mongo.MongoDB;
import util.RobotMessager;
import util.SimpleJsonParser;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

@OnApplicationStart 
public class Bootstrap extends Job {

	public void doJob() {
		DataParser.parsers.put("application/json", new SimpleJsonParser());
		 com.mongodb.DB db = MongoDB.db();
         DBCollection coll = db.getCollection("m_paster");
         coll.ensureIndex(new BasicDBObject("creator", 1));
         if(Boolean.valueOf(Play.configuration.getProperty("robot.enabled", "false"))) {
        	 RobotMessager.init(Play.configuration.getProperty("robot.msnaccount"), Play.configuration.getProperty("robot.password"));
        	 RobotMessager.login();
         }
	}
}
