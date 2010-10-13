package play.modules.mongo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ObjectId;

public class MongoDB {

	private static Mongo mongo;
	private static DB db;

	private static String host;
	private static Integer port;
	private static String dbname;

	/**
	 * Obtain a reference to the mongo database.
	 * 
	 * @return - a reference to the Mongo database
	 */
	public static DB db() {
		if (db==null){
			if(Play.configuration.containsKey("mongo.username") && Play.configuration.containsKey("mongo.password")){
				String username = Play.configuration.getProperty("mongo.username");
				String passwd = Play.configuration.getProperty("mongo.password");
				init(username, passwd);
			}
			else{
				init();
			}
		}

		return db;
	}

	/**
	 * Static initialiser.
	 * 
	 * @throws UnknownHostException
	 * @throws MongoException
	 */
	public static void init() {		

		if (host == null || port == null || dbname == null){
			host = Play.configuration.getProperty("mongo.host", "localhost");
			port = Integer.parseInt(Play.configuration.getProperty("mongo.port", "27017"));
			dbname = Play.configuration.getProperty("mongo.database", "play." + Play.configuration.getProperty("application.name"));
		}

		Logger.info("initializing DB ["+host+"]["+port+"]["+dbname+"]");

		try {
			mongo = new Mongo(host, port);
			db = mongo.getDB(dbname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public static void init(String username, String password){
		init();
		db.authenticate(username, password.toCharArray());
	}

	/**
	 * Creates an index.
	 * 
	 * @param collectionName
	 * @param indexString
	 */
	public static void index(String collectionName, String indexString){
		DBCollection c = db().getCollection(collectionName);
		DBObject indexKeys = createOrderDbObject(indexString);
		c.ensureIndex(indexKeys);
	}

	/**
	 * Removes an index. 
	 * 
	 * @param collectionName
	 * @param indexString
	 */
	public static void dropIndex(String collectionName, String indexString){
		DBCollection c = db().getCollection(collectionName);
		DBObject indexKeys = createOrderDbObject(indexString);
		c.dropIndex(indexKeys);
	}

	/** 
	 * Removes all indexes.
	 * 
	 * @param collectionName
	 */
	public static void dropIndexes(String collectionName){
		DBCollection c = db().getCollection(collectionName);
		c.dropIndexes();
	}

	/**
	 * Return a list of index names.
	 * 
	 * @param collectionName
	 * @return
	 */
	public static String[] getIndexes(String collectionName){
		List<String> indexNames = new ArrayList<String>();
		DBCollection c = db().getCollection(collectionName);
		List<DBObject> indexes = c.getIndexInfo();
		for (DBObject o : indexes){
			indexNames.add((String)o.get("name"));
		}

		return indexNames.toArray(new String[indexNames.size()]);
	}

	/**
	 * Adds a user to the database. We must manually set the readOnly parameter
	 * because the java mongo API does not yet support it. It will only work
	 * with database versions > 1.3.
	 * 
	 * @param username
	 * @param passwd
	 * @param readOnly
	 */
	public static void addUser(String username, String passwd, boolean readOnly){
		db().addUser(username, passwd.toCharArray());
		DBCollection c = db().getCollection("system.users");

		DBObject userObj = c.findOne(new BasicDBObject("user", username));
		if (userObj != null){
			userObj.put("readOnly", readOnly);
			c.save(userObj);
		}
	}

	/**
	 * Removes a user from the database.
	 * 
	 * @param username
	 */
	public static void removeUser(String username){
		DBCollection c = db().getCollection("system.users");

		DBObject userObj = c.findOne(new BasicDBObject("user", username));
		if (userObj != null){
			c.remove(userObj);
		}
	}

	/**
	 * Authenticates a user against a database.
	 * 
	 * @param username
	 * @param password
	 */
	public static boolean authenticate(String username, String password){
		return db().authenticate(username, password.toCharArray());
	}


	/**
	 * Counts the records in the collection.
	 * 
	 * @param collectionName
	 * @return - number of records in the collection
	 */
	public static long count(String collectionName){		
		return db().getCollection(collectionName).getCount();
	}

	/**
	 * Counts the records in the collection matching the query string.
	 * 
	 * @param collectionName - the queried collection
	 * @param query - the query string
	 * @param params - parameters for the query string
	 * @return
	 */
	public static long count(String collectionName, String query, Object[] params){
		return db().getCollection(collectionName).getCount(createQueryDbObject(query, params));
	}

	/**
	 * Provides a cursor to the objects in a collection, matching the query string.
	 * 
	 * @param collectionName - the target collection
	 * @param query - the query string
	 * @param params - parameters for the query
	 * @param clazz - the type of MongoModel
	 * @return - a mongo cursor
	 */
	@SuppressWarnings("unchecked")
	public static MongoCursor find(String collectionName, String query, Object[] params, Class clazz){
		return new MongoCursor(db().getCollection(collectionName).find(createQueryDbObject(query, params)),clazz);
	}

	/**
	 * Provides a cursor to the objects in a collection.
	 * 
	 * @param collectionName - the target collection
	 * @param clazz - the type of MongoModel
	 * @return - a mongo cursor
	 */
	@SuppressWarnings("unchecked")
	public static MongoCursor find(String collectionName, Class clazz){
		return new MongoCursor(db().getCollection(collectionName).find(),clazz);
	}

	/**
	 * Saves a model to its collection.
	 * @param <T> - the type of MongoModel to save
	 * @param collectionName - the collection to save it to
	 * @param model - the model to save
	 * @return - an instance of the model saved
	 */
	public static <T extends MongoModel> T save(String collectionName, T model){
		/* 
		 * Perhaps it would be better to immediately save the object to the database and assign its id. 
		 * 
		 */
		//		DBObject dbObject = new BasicDBObject(MongoMapper.convertValue(model, Map.class));
		DBObject dbObject = createDBObject(model);

		if (model.get_id() == null){
			db().getCollection(collectionName).insert(dbObject);
			model.set_id(ObjectId.massageToObjectId(dbObject.get("_id")));
		}
		else{
			dbObject.removeField("_id");
			db().getCollection(collectionName).update(new BasicDBObject("_id",model.get_id()), dbObject);
		}

		return model;
	}



	private static <T extends Object> DBObject createDBObject(T model) {
		Logger.debug("Into DBOject with " + model.getClass().getSimpleName() + " valued " + model.toString());
		DBObject dbObj = new BasicDBObject();

		try {
			if (MongoHelperUtils.isMongoEntity(model.getClass())) {
				for (Field field : model.getClass().getFields()) {
					String fieldName = field.getName();
					Object fieldValue = field.get(model);

					if (fieldValue == null) {
						Logger.debug(fieldName + " is null. Skipping");
						continue;
					}

					//                              Logger.debug("F: " + field.getName() + " " + field.getType() + " generic: " + field.getGenericType().getClass());

					// If it is a simple type like String, Integer, Double, Date, just save
					if (MongoHelperUtils.isString(field.getType())) {
						Logger.debug(fieldName + " is simple and has value " + fieldValue);
						dbObj.put(fieldName, fieldValue);
					} else if (MongoHelperUtils.isNumber(field.getType())) {
						Logger.debug(fieldName + " is number and has value " + fieldValue);
						dbObj.put(fieldName, (Number) fieldValue);
					} else if (MongoHelperUtils.isDate(field.getType())) {
						Logger.debug(fieldName + " is date and has value " + fieldValue);
						dbObj.put(fieldName, (Date) fieldValue);

						// if is collection
					} else if (MongoHelperUtils.isCollection(field.getType())) {
						Logger.debug("MONGODB: Storing collection");

						BasicDBList dbObjList = new BasicDBList();
						Collection col = (Collection) fieldValue;
						for (Iterator iterator = col.iterator(); iterator.hasNext();) {
							Object obj = iterator.next();
							if (MongoHelperUtils.isMongoEntity(obj.getClass())) {
								
								// Decide now whether to store the object or a dbref
								if (field.getAnnotation(MongoEmbedded.class) != null) {
									DBObject listDbObj = createDBObject(obj);
									dbObjList.add(listDbObj);
								} else {
									// Must be called per reflection! otherwise the non enhanced method seems to be called
									String collectionName = getCollectionByReflection(fieldValue); 
									String id = getObjectIdByReflection(fieldValue);

									if (id == null) {
										Logger.debug("Will not set a db reference if included reference objects are not saved before");
									} else {
										DBRef dbRef = new DBRef(db(), collectionName, id);
										dbObjList.add(dbRef);
									}
								}

								
							} else if (MongoHelperUtils.isString(obj.getClass())) {
								dbObjList.add(obj.toString());
							} else if (MongoHelperUtils.isNumber(obj.getClass())) {
								dbObjList.add((Number) obj);
							} else if (MongoHelperUtils.isDate(obj.getClass())) {
								dbObjList.add((Date) obj);
							}
						}
						
						// Finally add the whole list to the dbobj
						dbObj.put(fieldName, dbObjList);

						// TODO: if is map
						//				} else if (isMap(field.getType())) {
						//					Logger.debug("Map for " + fieldName);
						//					// Currently this only supports Map<String, String> types
						//					dbObj.put(fieldName, fieldValue);

						// if it is another mongosupport entity, we need a db ref, or it is marked embedded
					} else if (MongoHelperUtils.isMongoEntityBySuperclass(field.getType())) {
						// if @MongoEmbedded, then store inside
						// else store as dbref
						if (field.getAnnotation(MongoEmbedded.class) != null) {
							DBObject embeddedObj = createDBObject(fieldValue);
							dbObj.put(fieldName, embeddedObj);
						} else {
							// Must be called per reflection! otherwise the non enhanced method seems to be called
							String collectionName = getCollectionByReflection(fieldValue); 
							String id = getObjectIdByReflection(fieldValue);

							if (id == null) {
								Logger.debug("Will not set a db reference if included reference objects are not saved before");
								throw new RuntimeException("Will not set a db reference if included reference objects are not saved before");
							} else {
								DBRef dbRef = new DBRef(db(), collectionName, id);
								dbObj.put(fieldName, dbRef);
								Logger.debug("DBRef for " + fieldName);
							}
						}
					} else {
						Logger.debug("Unknown " + fieldName + " with type " + field.getType());                                   
					}
				}
			}
		} catch (IllegalArgumentException e) {
			Logger.error(e, "Error occured in creating MongoDBObject");
		} catch (IllegalAccessException e) {
			Logger.error(e, "Error occured in creating MongoDBObject");
		} catch (NullPointerException e) {
			Logger.error(e, "Error occured in creating MongoDBObject");
		} catch (UnsupportedOperationException e) {
			Logger.error(e, "Error occured in creating MongoDBObject");
		} catch (SecurityException e) {
			Logger.error(e, "Error occured in creating MongoDBObject");
		}


		return dbObj;
	}

	private static String getCollectionByReflection(Object obj) {
		String collectionName = MongoEnhancer.DEFAULT_COLLECTION_NAME;
		try {
			collectionName = obj.getClass().getDeclaredMethod("getCollectionName").invoke(obj, new Object[]{}).toString();
		} catch (Exception e) {
			Logger.error("Error on reflection call on getCollectionName", e);
		}
		return collectionName;
	}
	
	private static String getObjectIdByReflection(Object obj) {
		try {
			String id = obj.getClass().getDeclaredMethod("get_id").invoke(obj, new Object[]{}).toString();
			return id;
		} catch (Exception e) {
			Logger.error("Error on reflection call on get_id", e);
		}
		return null;
	}
	
	private static String getCollectionName(Class clazz) {

		MongoEntity mongoAnnotation = (MongoEntity) clazz.getAnnotation(MongoEntity.class);
		if (StringUtils.isNotEmpty(mongoAnnotation.value())) {
			return mongoAnnotation.value();
		}

		return "default";
	}


	/**
	 * Deletes a model from a collection.
	 * 
	 * @param <T> - the type of model
	 * @param collectionName - the collection
	 * @param model - the model
	 */
	public static <T extends MongoModel> void delete (String collectionName, T model){
		DBObject dbObject = new BasicDBObject("_id", model.get_id());
		db().getCollection(collectionName).remove(dbObject);
	}

	/**
	 * Deletes models from a collection that match a specific query string
	 * 
	 * @param collectionName - the collection 
	 * @param query - the query string
	 * @param params - parameters for the query string
	 * @return - the number of models deleted
	 */
	public static long delete (String collectionName, String query, Object[] params){
		DBObject dbObject = createQueryDbObject(query, params);
		long deleteCount = db().getCollection(collectionName).getCount(dbObject);
		db().getCollection(collectionName).remove(dbObject);

		return deleteCount;
	}

	/**
	 * Deletes all models from the collection.
	 * 
	 * @param collectionName - the collection
	 * @return - the number of models deleted
	 */
	public static long deleteAll (String collectionName){
		long deleteCount = count(collectionName);
		db().getCollection(collectionName).drop();
		return deleteCount;
	}

	/**
	 * Creates a query object for use with other methods
	 * 
	 * @param query - the query string
	 * @param values - values for the query
	 * @return - a DBObject representing the query
	 */
	public static DBObject createQueryDbObject(String query, Object[] values){

		String keys = extractKeys(query);

		DBObject object = new BasicDBObject(); 	
		String [] keyList = keys.split(",");

		if (keyList.length > values.length){
			throw new IllegalArgumentException("Not enough values for the keys provided");
		}

		for (int i = 0; i < keyList.length; i++){
			String key = keyList[i].trim();
			Object val = values[i];

			if ("_id".equals(key)) {
				object.put(key, ObjectId.massageToObjectId(val));
			} else {
//				if (val instanceof ObjectId) {
//					object.put(key, ObjectId.massageToObjectId(val));					
//				} else {
				object.put(key, val);
//				}
			}
		}

		Logger.debug("QUERYOBJ: %s", object);
		return object;
	}

	/**
	 * Creates an ordering object for use with other methods
	 * 
	 * @param query - the query string
	 * @param values - values for the query
	 * @return - a DBObject representing the ordering
	 */
	public static DBObject createOrderDbObject(String query){

		String keys = extractKeys(query);

		DBObject object = new BasicDBObject(); 	
		String [] keyList = keys.split(",");

		for (int i = 0; i < keyList.length; i++){

			int value = 1;
			if (keyList[i].charAt(0) == '-'){
				value = -1;
				keyList[i] = keyList[i].substring(1);
			}

			object.put(keyList[i].trim(), value);
		}  

		return object;
	}

	/**
	 * Extracts parameter names from a query string
	 * 
	 * @param queryString - the query string
	 * @return - a comma seperated string of parameter names
	 */
	private static String extractKeys(String queryString){
		queryString = queryString.substring(2);
		List<String> keys = new ArrayList<String>();
		String[] parts = queryString.split("And");
		for (String part : parts){
			if (part.charAt(0) == '-'){
				keys.add((part.charAt(0) + "") + (part.charAt(1) + "").toLowerCase() + part.substring(2));
			}
			else{
				keys.add((part.charAt(0) + "").toLowerCase() + part.substring(1));
			}
		}
		return StringUtils.join(keys.toArray(), ",");
	}

} 
