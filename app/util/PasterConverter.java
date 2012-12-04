package util;

import models.Paster;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import play.modules.search.DocumentConverter;

/**
 * Created by IntelliJ IDEA.
 * User: nile
 * Date: 11-3-30
 * Time: 下午10:21
 * To change this template use File | Settings | File Templates.
 */
public class PasterConverter implements DocumentConverter{
    public Document toDocument(Object object) throws Exception{
        if(!(object instanceof Paster))
            return null;
        Paster paster = (Paster) object;
        if(paster.type!= Paster.Type.Q)
            return null;
       Document document = new Document();
        document.add(new Field("_docID", paster.getId() + "", Field.Store.YES, Field.Index.NOT_ANALYZED));
//        document.add(new Field("_docID", getIdValueFor(paster) + "", Field.Store.YES, Field.Index.UN_TOKENIZED));
       document.add(new Field("title", paster.title, Field.Store.NO,Field.Index.ANALYZED));
       StringBuffer allcontent = new StringBuffer(paster.wiki);
        for (Paster c : paster.getComments()) {
                allcontent.append('\n').append(c.wiki);
        }
        for (Paster p : paster.getAnswers()) {
            allcontent.append(p.wiki);
            for (Paster c : p.getComments()) {
                allcontent.append('\n').append(c.wiki);
            }
        }
       document.add(new Field("content", allcontent.toString(), Field.Store.NO,Field.Index.ANALYZED));
       //document.add(new Field("tagstext", paster.tagstext, Field.Store.NO,Field.Index.ANALYZED));
       return document;
    }
}
