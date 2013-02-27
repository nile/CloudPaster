package jobs;

import models.Paster;
import play.jobs.Job;
import util.IndexManager;

/**
 * Created with IntelliJ IDEA.
 * User: Black
 * Date: 13-2-27
 * Time: 上午11:17
 * To change this template use File | Settings | File Templates.
 */
public class IndexPasterJob  extends Job {
    Paster paster = null;
       public IndexPasterJob(Paster paster){
           this.paster = paster;
       }
       @Override
    public void doJob() throws Exception {
           IndexManager.index(paster);
    }
}
