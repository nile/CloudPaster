package play.modules.mongo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used by the MongoEnhancer to 
 * signal it to provide implementations for MongoModel
 * methods.
 * 
 * @author Andrew Louth
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MongoEntity {

	/**
	 * This value represents the collectionName.
	 * @return
	 */
	String value() default "default";

}
