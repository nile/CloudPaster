package play.modules.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import play.Logger;
import play.Play;
import play.classloading.ApplicationClasses.ApplicationClass;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.ObjectId;

/**
 * Provides Mongo DBCursor functionality.
 * 
 * @author Andrew Louth
 */
public class MongoCursor {

	public DBCursor cursor;
	@SuppressWarnings("unchecked")
	public Class clazz;

	/**
	 * Constructor
	 * 
	 * @param cursor
	 * @param clazz
	 */
	@SuppressWarnings("unchecked")
	public MongoCursor(DBCursor cursor, Class clazz){
		this.cursor = cursor;
		this.clazz = clazz;
	}

	/**
	 * Retrieves a list of MongoModels. 
	 * 
	 * @param <T> - the specific type of MongoModel
	 * @param page - the offset
	 * @param length - the length of a page
	 * @return - the list of MongoModel types
	 */
	@SuppressWarnings("unchecked")
	public <T extends MongoModel> List<T> fetch(int page, int length){
		List<T> resultList = new ArrayList<T>();

		if (length != 0){
			cursor.limit(length);
		}

		if (page > 1){
			cursor.skip((page-1)*length);
		}

		while(cursor.hasNext()){

			DBObject dbObject = cursor.next();
			//			Map map = dbObject.toMap();

			try {
				//				ObjectId id = ObjectId.massageToObjectId(map.remove("_id"));
				//				T model = (T) MongoMapper.convertValue(map, clazz);
				T model = (T) convertValue(dbObject, clazz);

				resultList.add(model);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} 
		}

		return resultList;
	}


	public <T extends MongoModel> T convertValue(DBObject dbObject, Class<T> toValueType){
		Logger.debug("convertValue called for class %s and %s", toValueType, dbObject);

		if (dbObject == null || toValueType == null) {
			Logger.error("No DBObject or class to convert to supplied: db was %s - class was %s", dbObject, toValueType);
			return null;
		}


		try {
			T model = toValueType.newInstance();

			for (String key : dbObject.keySet()) {
				Object val = dbObject.get(key);

				// Dont do anythign if val is null
				if (val == null) {
					continue;
				}

				// Ignore _id
				if ("_id".equals(key)) {
					model.set_id(ObjectId.massageToObjectId(val));
					continue;
				}

				//				Logger.debug("Addind property " + key);

				Field field = model.getClass().getField(key);

				if (field != null) {
					Class fieldClass = field.getType();
					boolean fieldIsMongoEntity = MongoHelperUtils.isMongoEntityByAnnotation(field.getType());

					if (fieldIsMongoEntity) {
						Logger.debug("Convert dbObj " + field.getName() + " valued " + val.getClass().getSimpleName());
						if (val instanceof DBRef) {
							// Dereferencing the val results into null pointer exception
							// I Have no fucking clue why, some sort of lazy loading in the java driver
							// is my guess
							DBObject dbObj = MongoHelperUtils.dereferenceDbVal((DBRef)val);
							field.set(model, convertValue(dbObj, fieldClass));
						} else {
							field.set(model, convertValue((DBObject)val, fieldClass));
						}

					} else if (MongoHelperUtils.isCollection(field.getType())) {
						Logger.debug("GOING TO HANDLE COLLECTION: %s", field.getType().getSimpleName());
						Collection col = MongoHelperUtils.createCollection(field.getType());
						Logger.debug("Col is: %s and classed %s", col, col.getClass().getSimpleName());
						BasicDBList dbList = (BasicDBList) val;

						// Get the User from List<User> foobar;
						Class modelClass = MongoHelperUtils.getGenericClass(field);
						if (modelClass == null) {
							Logger.error("No generic type setting for field " + field.getName() + ". Not setting anything");
							continue;
						}

						for (Iterator iterator = dbList.iterator(); iterator.hasNext();) {
							Object obj = iterator.next();

							DBObject iteratedDbObj;
							if (obj instanceof DBRef) {
								iteratedDbObj = MongoHelperUtils.dereferenceDbVal((DBRef) obj);
							} else {
								iteratedDbObj = (DBObject) obj;
							}
							col.add(convertValue(iteratedDbObj, modelClass));
						}

						field.set(model, col);

					} else {
						Logger.debug("Calling setter for " + field.getName() + " valued " + val.getClass().getSimpleName());
						field.set(model, val);
					}
				}
			}

			return model;
		} catch (InstantiationException e) {
			Logger.error(e, "Error convertValue");
		} catch (IllegalAccessException e) {
			Logger.error(e, "Error convertValue");
		} catch (SecurityException e) {
			Logger.error(e, "Error convertValue");
		} catch (NoSuchFieldException e) {
			Logger.error(e, "Error convertValue");
		}

		return null;
	}



	/**
	 * Retrieves a list of MongoModels.
	 * 
	 * @param <T> - the specific type of MongoModel
	 * @param limit - the number of models to return
	 * @return - the list of MongoModel types
	 */
	public <T extends MongoModel> List<T> fetch(int limit){
		return fetch(1,limit);
	}

	/**
	 * Retrieves a list of MongoModels. This method will
	 * return all of the models reachable from this cursor.
	 * 
	 * @param <T> - the specific MongoModel type
	 * @return - the list of MongoModel types
	 */
	public <T extends MongoModel> List<T> fetch(){
		return fetch(0);
	}

	/**
	 * Return the first model in 
	 * @param <T> - the specific MongoModel type
	 * @return - one instance of a MongoModel
	 */
	@SuppressWarnings("unchecked")
	public <T extends MongoModel> T first(){
		List<T> resultList = fetch(1,1);
		if (resultList == null || resultList.size() == 0) {
			return null;
		}
		return resultList.get(0);
	}

	/**
	 * Skips the given the number of records.
	 * 
	 * @param from - the number of records to skip
	 * @return - the cursor
	 */
	public MongoCursor from(int from){
		cursor.skip(from);
		return this;
	}

	/**
	 * Orders the objects pointed to by the cursor, using the
	 * orderBy string.
	 * @param orderBy - the string determining the parameters to order by
	 * @return - the cursor
	 */
	public MongoCursor order(String orderBy){
		DBObject order = MongoDB.createOrderDbObject(orderBy);
		cursor.sort(order);
		return this;
	}
}
