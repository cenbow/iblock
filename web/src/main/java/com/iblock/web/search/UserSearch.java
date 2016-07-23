package com.iblock.web.search;

import com.iblock.common.bean.Page;
import com.iblock.web.info.UserDisplayInfo;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * Created by baidu on 16/7/23.
 */
public class UserSearch implements Search<UserDisplayInfo> {
    public void init() {

    }

    public void update(UserDisplayInfo entity) {

    }

    public Page<UserDisplayInfo> search(Condition condition) {
        return null;
    }

    private static void create(StandardAnalyzer analyzer, Directory index) throws IOException {

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDoc(w, "Lucene,in,Action", "193398817");
        addDoc(w, "Lucene for Dummies", "55320055Z");
        addDoc(w, "Managing Gigabytes", "55063554A");
        addDoc(w, "The Art of Computer Science", "9900333X");
        w.close();
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
}
