package play.modules.mongo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to check whether an mongoentity should be stored as an embedded object
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoEmbedded {
}
