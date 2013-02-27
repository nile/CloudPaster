import com.avaje.ebean.Query;
import com.avaje.ebean.QueryIterator;
import jobs.IndexPasterJob;
import models.CPRole;
import models.ConfigItem;
import models.Paster;
import play.Logger;
import play.Play;
import play.data.parsing.DataParser;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.jobs.OnApplicationStop;
import util.IndexManager;
import util.RobotMessager;
import util.SimpleJsonParser;

import java.util.List;

@OnApplicationStop
public class Shutdown extends Job {

	public void doJob() {
        Logger.info("Shutdown hook");
        IndexManager.stop();
	}
}
