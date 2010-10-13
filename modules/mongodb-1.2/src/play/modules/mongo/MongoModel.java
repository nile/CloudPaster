package play.modules.mongo;

import com.mongodb.ObjectId;

/**
 * This class provides the abstract declarations for all MongoModels.
 * Implementations of these declarations are provided by the MongoEnhancer.
 * 
 * @author Andrew Louth
 */
public class MongoModel {
	
    public ObjectId get_id() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public void set_id(ObjectId _id) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
	
    public static String getCollectionName() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }

    public static long count() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static long count(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static MongoCursor find(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static MongoCursor find() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public <T extends MongoModel> T save() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public void delete() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static long delete(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static long deleteAll(){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static void index(String indexString){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static void dropIndex(String indexString){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static void dropIndexes(){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static String[] getIndexes(){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
}
