package play.modules.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.type.PrimitiveType;

import play.Logger;
import play.Play;
import play.classloading.ApplicationClasses.ApplicationClass;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.ObjectId;

public class MongoHelperUtils {

	private MongoHelperUtils() {}
	
	public static DBObject dereferenceDbVal(DBRef dbRef) {
		Logger.debug("DBRef is %s - %s - %s", dbRef, dbRef.getId(), dbRef.getRef());
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(dbRef.getId().toString()));
		DBObject dbObj = MongoDB.db().getCollection(dbRef.getRef()).findOne(query);
		return dbObj;
	}
	
	
	public static Class getGenericClass(Field field) {
		ParameterizedType type = (ParameterizedType) field.getGenericType();
		String className = type.getActualTypeArguments()[0].toString().split(" ",2)[1];
		ApplicationClass appClass = Play.classes.getApplicationClass(className);
		Class modelClass = appClass != null ? appClass.javaClass : null;
		return modelClass;
	}

	
	
	public static boolean isDate(Class<?> type) {
		return type.isAssignableFrom(Date.class);
	}

	public static boolean isNumber(Class<?> type) {
		return (type.isPrimitive() && Byte.TYPE == type || Integer.TYPE==type ||Long.TYPE==type || Float.TYPE==type || Double.TYPE==type) 
		|| Number.class.isAssignableFrom(type);
	}
	public static boolean isString(Class<?> type) {
		return type.equals(String.class);
	}
	
	public static final boolean isCollection(Class<?> type) {
		return Arrays.asList(type.getInterfaces()).contains(Collection.class);
	}
	
	public static final boolean isSet(Class<?> type) {
		return Arrays.asList(type.getInterfaces()).contains(Set.class) || Set.class.equals(type);
	}

	public static final boolean isList(Class<?> type) {
		return Arrays.asList(type.getInterfaces()).contains(List.class) || List.class.equals(type);
	}
	
	public static final Collection createCollection(Class<?> type) {
		if (isSet(type)) {
			return new HashSet();
		}
		if (isList(type)) {
			return new ArrayList();
		}
		return null;
	}

	public static boolean isMongoEntity(Class<?> clazz) {
		return isMongoEntityByAnnotation(clazz) && isMongoEntityByAnnotation(clazz);
	}
	
	public static boolean isMongoEntityBySuperclass(Class<?> type) {
		return MongoModel.class.equals(type.getSuperclass());
	}
	
	public static boolean isMongoEntityByAnnotation(Class<?> type) {
		return type.isAnnotationPresent(MongoEntity.class);
	}

	//	private static boolean isMap(Class<?> type) {
	//		return type.isAssignableFrom(Map.class);
	//	}





}
