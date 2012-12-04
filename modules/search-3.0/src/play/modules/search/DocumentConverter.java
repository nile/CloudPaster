package play.modules.search;

import org.apache.lucene.document.Document;

/**
 * Created by IntelliJ IDEA.
 * User: nile
 * Date: 11-3-30
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
public interface DocumentConverter {
    Document toDocument(Object object) throws Exception;
}
