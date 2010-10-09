package play.modules.mongosearch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.FSDirectory;

import play.Logger;
import play.Play;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.exceptions.UnexpectedException;
import play.modules.mongo.MongoDB;
import play.modules.mongo.MongoEntity;
import play.modules.mongo.MongoModel;

/**
 * Very basic tool to basic search on your JPA objects.
 * 
 * On a JPAModel subclass, add the @Indexed annotation on your class, and the @Field
 * annotation on your field members
 * 
 * Each time you save, update or delete your class, the corresponding index is
 * updated
 * 
 * use the search method to query an index.
 * 
 * Samples in samples-and-tests/app/controllers/JPASearch.java
 */
public class MongoSearch {

    private static Map<String, IndexWriter> indexWriters = new HashMap<String, IndexWriter>();
    private static Map<String, IndexSearcher> indexReaders = new HashMap<String, IndexSearcher>();

    public static String DATA_PATH;
    private static String ANALYSER_CLASS;
    public static boolean sync = true;

    public static void init() {
        try {
            shutdown();
        } catch (Exception e) {
            Logger.error(e, "Error while shutting down search module");
        }

        ANALYSER_CLASS = Play.configuration.getProperty("play.search.analyser", "org.apache.lucene.analysis.standard.StandardAnalyzer");
        if (Play.configuration.containsKey("play.search.path"))
            DATA_PATH = Play.configuration.getProperty("play.search.path");
        else
            DATA_PATH = Play.applicationPath.getAbsolutePath() + "/data/search/";
        Logger.trace("Search module repository is in " + DATA_PATH);
        Logger.trace("Write operations synch: " + sync);
        sync = Boolean.parseBoolean(Play.configuration.getProperty("play.search.synch", "true"));
    }

    private static Analyzer getAnalyser() {
        try {
            Class clazz = Class.forName(ANALYSER_CLASS);
            return (Analyzer) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class SearchException extends RuntimeException {
        public SearchException(String message, Throwable cause) {
            super(message, cause);
        }

        public SearchException(Throwable cause) {
            super(cause);
        }

        public SearchException(String message) {
            super(message);
        }
    }

    public static class QueryResult {
        public String id;
        public float score;
        public MongoModel object;
    }

    public static class Query {
        private Class clazz;
        private String query;
        private String[] order = new String[0];
        private int offset = 0;
        private int pageSize = 10;
        private boolean reverse = false;
        private Hits hits = null;
        private String collectionName;

        protected Query(String query, Class clazz) {
            this.query = query;
            this.clazz = clazz;
            MongoEntity annotation = (MongoEntity) clazz.getAnnotation(MongoEntity.class);
            if(annotation!=null)
            	collectionName = annotation.value();
        }

        public Query page(int offset, int pageSize) {
            this.offset = offset;
            this.pageSize = pageSize;
            return this;
        }

        public Query all() {
            pageSize = -1;
            return this;
        }

        public Query reverse() {
            this.reverse = true;
            return this;
        }

        public Query orderBy(String... order) {
            this.order = order;
            return this;
        }

        private Sort getSort() {
            Sort sort = new Sort();
            if (order.length > 0) {
                if (reverse) {
                    if (order.length != 1)
                        throw new SearchException("reverse can be used while sorting only one field with oderBy");
                    else
                        sort.setSort(order[0], reverse);
                } else
                    sort.setSort(order);
            }
            return sort;
        }

        /**
         * Executes the query and return directly JPAModel objects (No score
         * information)
         * 
         * @return
         */
        public <T extends MongoModel> List<T> fetch() throws SearchException {
            try {
                List<QueryResult> results = executeQuery(true);
                List<MongoModel> objects = new ArrayList<MongoModel>();
                for (QueryResult queryResult : results) {
                    objects.add(queryResult.object);
                }
                return (List) objects;
            } catch (Exception e) {
                throw new UnexpectedException(e);
            }
        }

        public List<String> fetchIds() throws SearchException {
            try {
                List<QueryResult> results = executeQuery(false);
                List<String> objects = new ArrayList<String>();
                for (QueryResult queryResult : results) {
                    objects.add(queryResult.id);
                }
                return objects;
            } catch (Exception e) {
                throw new UnexpectedException(e);
            }
        }

        public long count() throws SearchException {
            try {
                org.apache.lucene.search.Query luceneQuery = new QueryParser("_docID", getAnalyser()).parse(query);
                hits = getIndexReader(clazz.getName()).search(luceneQuery, getSort());
                return hits.length();
            } catch (ParseException e) {
                throw new SearchException(e);
            } catch (Exception e) {
                throw new UnexpectedException(e);
            }
        }

        /**
         * Executes the lucene query against the index. You get QueryResults.
         * 
         * @param fetch
         *            load the corresponding JPAModel objects in the QueryResult
         *            Object
         * @return
         */
        public List<QueryResult> executeQuery(boolean fetch) throws SearchException {
            try {
                if (hits == null) {
                    org.apache.lucene.search.Query luceneQuery = new QueryParser("_docID", getAnalyser()).parse(query);
                    hits = getIndexReader(clazz.getName()).search(luceneQuery, getSort());
                }
                List<QueryResult> results = new ArrayList<QueryResult>();
                if (hits == null)
                    return results;

                int l = hits.length();
                if (offset > l) {
                    return results;
                }
                List<Long> ids = new ArrayList<Long>();
                
                if (pageSize > 0) {
                    for (int i = offset; i < (offset + pageSize > l ? l : offset + pageSize); i++) {
                        QueryResult qresult = new QueryResult();
                        qresult.score = hits.score(i);
                        qresult.id = hits.doc(i).get("_docID");
                        if (fetch) {
                            qresult.object = (MongoModel) MongoDB.find(collectionName,"by_id",new Object[] {qresult.id}, clazz).first();
                            if (qresult.object == null) {
//                                throw new SearchException("Please re-index");
                            	continue;
                            }
                        }
                        results.add(qresult);
                    }
                } else {
                    for (int i = 0; i < l; i++) {
                        QueryResult qresult = new QueryResult();
                        qresult.score = hits.score(i);
                        qresult.id = hits.doc(i).get("_docID");
                        if (fetch) {
                            qresult.object =  (MongoModel) MongoDB.find(collectionName,"by_id",new Object[] {qresult.id}, clazz).first(); 
                            if (qresult.object == null) {
//                                throw new SearchException("Please re-index");
                            	continue;
                            }
                        }
                        results.add(qresult);
                    }
                }
                return results;
            } catch (ParseException e) {
                throw new SearchException(e);
            } catch (Exception e) {
                throw new UnexpectedException(e);
            }
        }
    }

    public static Query search(String query, Class clazz) {
        return new Query(query, clazz);
    }
   
    public static void unIndex(Object object) {
        try {
            if (!(object instanceof MongoModel))
                return;
            if (object.getClass().getAnnotation(Indexed.class) == null)
                return;
            MongoModel jpaModel = (MongoModel) object;
            String index = object.getClass().getName();
            getIndexWriter(index).deleteDocuments(new Term("_docID", jpaModel.get_id().toString() + ""));
            if (sync) {
                getIndexWriter(index).flush();
                dirtyReader(index);
            }
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    public static void index(Object object) {
        try {
            if (!(object instanceof MongoModel))
                return;
            MongoModel jpaModel = (MongoModel) object;
            String index = object.getClass().getName();
            Document document = toDocument(object);
            if (document == null)
                return;
            getIndexWriter(index).deleteDocuments(new Term("_docID", jpaModel.get_id().toString() + ""));
            getIndexWriter(index).addDocument(document);
            if (sync) {
                getIndexWriter(index).flush();
                dirtyReader(index);
            }
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    private static Document toDocument(Object object) throws Exception {
        Indexed indexed = object.getClass().getAnnotation(Indexed.class);
        if (indexed == null)
            return null;
        if (!(object instanceof MongoModel))
            return null;
        MongoModel jpaModel = (MongoModel) object;
        Document document = new Document();
        document.add(new Field("_docID", jpaModel.get_id().toString() + "", Field.Store.YES, Field.Index.UN_TOKENIZED));
        for (java.lang.reflect.Field field : object.getClass().getFields()) {
        	play.modules.mongosearch.Field index = field.getAnnotation(play.modules.mongosearch.Field.class);
            if (index == null)
                continue;
            if (field.getType().isArray())
                continue;
            if (field.getType().isAssignableFrom(Collection.class))
                continue;

            String name = field.getName();
            String value = valueOf(object, field);

            if (value == null)
                continue;

            document.add(new Field(name, value, index.stored() ? Field.Store.YES : Field.Store.NO, index.tokenize() ? Field.Index.TOKENIZED
                    : Field.Index.UN_TOKENIZED));
        }
        return document;
    }

    private static String valueOf(Object object, java.lang.reflect.Field field) throws Exception {
        if (field.getType().equals(String.class))
            return (String) field.get(object);
        return "" + field.get(object);
    }

    public static IndexSearcher getIndexReader(String name) {
        try {
            if (!indexReaders.containsKey(name)) {
                synchronized (MongoSearch.class) {
                    File root = new File(DATA_PATH, name);
                    if (root.exists()) {
                        IndexSearcher reader = new IndexSearcher(FSDirectory.getDirectory(root));
                        indexReaders.put(name, reader);
                    } else
                        throw new UnexpectedException("Could not find " + name + " index. Please re-index");
                }
            }
            return indexReaders.get(name);
        } catch (Exception e) {
            throw new UnexpectedException("Cannot open index", e);
        }
    }

    /**
     * Used to synchronize reads after write
     * 
     * @param name
     *            of the reader to be reopened
     */
    public static void dirtyReader(String name) {
        synchronized (MongoSearch.class) {
            try {
                if (indexReaders.containsKey(name)) {
                    IndexReader rd = indexReaders.get(name).getIndexReader().reopen();
                    indexReaders.get(name).close();
                    indexReaders.remove(name);
                    indexReaders.put(name, new IndexSearcher(rd));
                }
            } catch (IOException e) {
                throw new UnexpectedException("Can't reopen reader", e);
            }
        }
    }

    private static IndexWriter getIndexWriter(String name) {
        try {
            if (!indexWriters.containsKey(name)) {
                synchronized (MongoSearch.class) {
                    File root = new File(DATA_PATH, name);
                    if (!root.exists())
                        root.mkdirs();
                    if (new File(root, "write.lock").exists())
                        new File(root, "write.lock").delete();
                    IndexWriter writer = new IndexWriter(FSDirectory.getDirectory(root), true, getAnalyser());
                    indexWriters.put(name, writer);
                }
            }
            return indexWriters.get(name);
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    public static void reindex() throws Exception {
        shutdown();
        File fl = new File(DATA_PATH);
        FileUtils.deleteDirectory(fl);
        fl.mkdirs();
        List<ApplicationClass> classes = Play.classes.getAnnotatedClasses(Indexed.class);
        for (ApplicationClass applicationClass : classes) {
            List<MongoModel> objects = (List<MongoModel>) MongoDB.find(applicationClass.javaClass.getAnnotation(MongoEntity.class).value(), applicationClass.javaClass);
            for (MongoModel model : objects) {
                index(model);
            }
        }
        Logger.info("Rebuild index finished");
    }

    public static void shutdown() throws Exception {
        for (IndexWriter writer : indexWriters.values()) {
            writer.close();
        }
        for (IndexSearcher searcher : indexReaders.values()) {
            searcher.close();
        }
        indexWriters.clear();
        indexReaders.clear();
    }
}
