package play.modules.mongosearch;

import play.Logger;
import play.PlayPlugin;
import play.exceptions.UnexpectedException;

public class MongoSearchPlugin extends PlayPlugin {
    
    @Override
	public void onApplicationStart() {
    	Logger.debug("MonogSearchPlugin init");
        MongoSearch.init();
	}

	@Override
    public void onApplicationStop() {
        try {
        	MongoSearch.shutdown();
        } catch (Exception e) {
            throw new UnexpectedException (e);
        }
    }

    @Override
    public void onEvent(String message, Object context) {
    	Logger.debug("OnEvent %s %s",message,context);
       
    }
}
