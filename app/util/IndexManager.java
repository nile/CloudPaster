package util;

import models.Paster;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.*;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import play.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author nile
 */
public class IndexManager {
    private final static String indexDir = "_index";

    public static List<Paster> search(String key, int from) {
        IndexSearcher srch = null;
        try {
            checkInit();
            IndexReader ir = IndexReader.open(fsDirectory);
            srch = new IndexSearcher(ir);
            IndexReader reader = srch.getIndexReader();
            QueryParser parser = new QueryParser(Version.LUCENE_36, "content", analyzer);
            String[] strings = TokenUtil.token(key);
            BooleanQuery booleanQuery = new BooleanQuery();
            for (int i = 0; i < strings.length; i++) {
                Logger.info(strings[i]);
                String string = strings[i];
                booleanQuery.add(new TermQuery(new Term("title", string)), Occur.SHOULD);
                booleanQuery.add(new TermQuery(new Term("content", string)), Occur.SHOULD);
            }


            FastVectorHighlighter highlighter = new FastVectorHighlighter();
            FieldQuery fieldQuery = highlighter.getFieldQuery(booleanQuery);
            TopDocs hits = srch.search(booleanQuery, 1000);
            List<Paster> files = new LinkedList<Paster>();

            for (int i = from; i < Math.min(from + 10, hits.totalHits); i++) {
                ScoreDoc scoreDoc = hits.scoreDocs[i];
                Document doc = srch.doc(scoreDoc.doc);
                Paster jf = null;// new Paster();
                String snippet = highlighter.getBestFragment(fieldQuery, reader, scoreDoc.doc, "content", 100);
//            jf.id = ;
//            jf.title = doc.get("title");
//            jf.snippet = snippet;
                jf = Paster.findById(Long.parseLong(doc.get("obj_id").substring("paster_".length())));
                files.add(jf);
            }
            return files;
        } catch (Exception e) {
            Logger.info(e, e.getLocalizedMessage());
        } finally {
            try {
                if (srch != null) srch.close();
            } catch (IOException e) {
                Logger.info(e, e.getLocalizedMessage());
            }
        }
        return Collections.emptyList();
    }

    private static int docCount() throws IOException {
        File index_dir = new File(indexDir);
        if (!index_dir.exists() || index_dir.list().length == 0)
            return 0;

        IndexReader indexReader = org.apache.lucene.index.IndexReader.open(fsDirectory, true);
        int count = indexReader.maxDoc();
        indexReader.close();
        return count;
    }

    static FSDirectory fsDirectory;
    static IndexWriter indexWriter;

    private static boolean checkInit() {
        File index_dir = new File(indexDir);
        if (!index_dir.exists()) {
            index_dir.mkdirs();
        }
        if(indexWriter==null || fsDirectory == null)
        try {
            fsDirectory = FSDirectory.open(new File(indexDir));
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36, analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(fsDirectory, iwc);
        } catch (IOException e) {
            Logger.info(e, e.getMessage());
        }
        return true;
    }

    /**
     * create index
     */
    public static boolean index(Paster paster) {
        checkInit();
        if (paster == null)
            return false;
        try {
            indexWriter.deleteDocuments(new Term("obj_id", "paster_" + paster.id));
            indexWriter.addDocument(indexPaster(paster));
            indexWriter.forceMerge(1);
            indexWriter.commit();
            return true;
        } catch (IOException e) {
            Logger.info(e, e.getLocalizedMessage());
        } finally {

        }
        return false;
    }

    static Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_36);

    public static boolean unindex(Paster paster) {
        try {
            checkInit();


            indexWriter.deleteDocuments(new Term("obj_id", "paster_" + paster.id));
            indexWriter.commit();

            return true;
        } catch (IOException e) {
            Logger.info(e, e.getLocalizedMessage());
        } finally {

        }
        return false;
    }

    /**
     * Add one document to the Lucene index
     *
     * @throws IOException
     */
    private static Document indexPaster(Paster paster) throws IOException {
        String path = "paster_" + paster.getId();
        String title = paster.title;
        String content = paster.content;
        List<Paster> comments = paster.getComments();
        for (Paster comment : comments) {
            content += comment.content;
        }
        List<Paster> answers = paster.getAnswers();
        for (Paster answer : answers) {
            content += answer.content;
            List<Paster> answerComments = paster.getComments();
            for (Paster answerComment : answerComments) {
                content += answerComment.content;
            }
        }
        String tags = paster.tagstext;
        content = Jsoup.clean(content, Whitelist.basic());
        Document document = new Document();
        document.add(new Field("obj_id", path, Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("tags", tags, Field.Store.YES, Index.ANALYZED));
        document.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("content", content, Field.Store.YES, Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));

        return document;

    }

    public static void stop() {
        try {
            indexWriter.close();
            fsDirectory.close();
        } catch (IOException e) {
            Logger.error(e,e.getMessage());
        }
    }
}
