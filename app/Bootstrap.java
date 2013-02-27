import java.util.List;

import com.avaje.ebean.Query;

import com.avaje.ebean.QueryIterator;
import jobs.IndexPasterJob;
import models.CPRole;
import models.ConfigItem;
import models.Paster;
import org.apache.commons.lang.StringUtils;
import play.Play;
import play.data.parsing.DataParser;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.jobs.OnApplicationStop;
import play.modules.ebean.EbeanSupport;
import util.RobotMessager;
import util.SimpleJsonParser;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        DataParser.parsers.put("application/json", new SimpleJsonParser());
        if (Boolean.valueOf(Play.configuration.getProperty("robot.enabled", "false"))) {
            RobotMessager.init(Play.configuration.getProperty("robot.msnaccount"), Play.configuration.getProperty("robot.password"));
            RobotMessager.login();
        }
        //应用系统配置
        final List<ConfigItem> configs = ConfigItem.listAll();
        for (ConfigItem item : configs) {
            Play.configuration.put(item.name, item.val);
        }
        //创建角色
        CPRole.createOrGet("user");
        CPRole.createOrGet("admin");
        CPRole.createOrGet("guest");
        rebuildIndex();
    }

    private void rebuildIndex() {
        if (StringUtils.equalsIgnoreCase(Play.configuration.getProperty("search.reindex"), "true")) {
            Query<Paster> query = Paster.find("type = ?", Paster.Type.Q);
            QueryIterator<Paster> iterate = query.findIterate();
            while (iterate.hasNext()) {
                Paster paster = iterate.next();
                new IndexPasterJob(paster).in(1);
            }
        }
    }
}
