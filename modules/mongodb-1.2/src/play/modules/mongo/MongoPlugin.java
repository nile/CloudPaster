package play.modules.mongo;

import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;

/**
 * The plugin for the Mongo module.
 * 
 * @author Andrew Louth
 */
public class MongoPlugin extends PlayPlugin {
	
	private MongoEnhancer enhancer = new MongoEnhancer();

	@Override
	public void enhance(ApplicationClass applicationClass) throws Exception {
		enhancer.enhanceThisClass(applicationClass);
	}
}
