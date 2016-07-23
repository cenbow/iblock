package com.iblock.web.search;

import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.dao.po.Project;
import com.iblock.service.project.ProjectService;
import com.iblock.web.info.ProjectSimpleInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.IntPoint;
//import org.apache.lucene.document.LongPoint;
//import org.apache.lucene.document.StringField;
//import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Created by baidu on 16/7/23.
 */
@Component
public class ProjectSearch implements Search<ProjectSimpleInfo> {

    @Resource
    private ProjectService projectService;

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private Directory index = new RAMDirectory();

    public void init() {
        ProjectSearchBean bean = new ProjectSearchBean();
        bean.setOffset(0);
        bean.setPageSize(100000000);
        Page<Project> page = projectService.search(bean);
//        projectService.getSkills()
        if (CollectionUtils.isNotEmpty(page.getResult())) {
            return;
        }

    }

    public void update(ProjectSimpleInfo entity) {

    }

    public Page<ProjectSimpleInfo> search(Condition condition) {
        return null;
    }

    private void create(StandardAnalyzer analyzer, Directory index, List<Project> list) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        for (Project p : list) {
            addDoc(w, p);
        }
        w.close();
    }

    private void addDoc(IndexWriter w, Project p) throws IOException {
        Document doc = new Document();
//        doc.add(new StringField("name", p.getName(), Field.Store.YES));
//        doc.add(new LongPoint("managerId", p.getManagerId()));
//        doc.add(new LongPoint("agentId", p.getAgentId()));
//        doc.add(new IntPoint("minPay", p.getMinPay()));
//        doc.add(new IntPoint("maxPay", p.getMaxPay()));
//        doc.add(new IntPoint("resident", p.getResident() ? 1 : 0));
//        doc.add(new IntPoint("status", isbn, Field.Store.YES));
//        doc.add(new IntPoint("city", isbn, Field.Store.YES));
//        doc.add(new IntPoint("industry", isbn, Field.Store.YES));
//        doc.add(new StringField("skill", isbn, Field.Store.YES));
//        doc.add(new StringField("freeze", isbn, Field.Store.YES));
//        doc.add(new StringField("json", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
}
