package com.iblock.service.search;

import com.google.gson.Gson;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.utils.DateUtils;
import com.iblock.dao.ProjectDao;
import com.iblock.dao.ProjectSkillDao;
import com.iblock.dao.po.City;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.ProjectSkill;
import com.iblock.service.meta.MetaService;
import com.iblock.service.project.ProjectService;
import com.iblock.service.user.UserService;
import com.iblock.service.info.KVInfo;
import com.iblock.service.info.KVLongInfo;
import com.iblock.service.info.ProjectSimpleInfo;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baidu on 16/7/23.
 */
@Component
@Log4j
public class ProjectSearch {

    @Resource
    private ProjectDao projectDao;
    @Resource
    private ProjectSkillDao projectSkillDao;
    @Resource
    private MetaService metaService;
    @Resource
    private UserService userService;

    private Map<Integer, String> cityMap;
    private Map<Integer, String> industryMap;
    private Map<Long, ArrayList<ProjectSkill>> skillMap;

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private Directory index = new RAMDirectory();

    @PostConstruct
    public void init() throws Exception {
        ProjectSearchBean bean = new ProjectSearchBean();
        bean.setOffset(0);
        bean.setPageSize(100000000);
        List<Project> list = projectDao.list(bean);
        List<ProjectSkill> skillList = projectSkillDao.selectAll();
        skillMap = new HashMap<Long, ArrayList<ProjectSkill>>();
        if (CollectionUtils.isNotEmpty(skillList)) {
            for (ProjectSkill skill : skillList) {
                if (!skillMap.containsKey(skill.getProjectId())) {
                    skillMap.put(skill.getProjectId(), new ArrayList<ProjectSkill>());
                }
                skillMap.get(skill.getProjectId()).add(skill);
            }
        }
        List<City> cities = metaService.getCities(null);
        List<Industry> industries = userService.getIndustries();
        cityMap = new HashMap<Integer, String>();
        industryMap = new HashMap<Integer, String>();
        if (CollectionUtils.isNotEmpty(cities)) {
            for (City city : cities) {
                cityMap.put(city.getCityId(), city.getCityName());
            }
        }
        if (CollectionUtils.isNotEmpty(industries)) {
            for (Industry industry : industries) {
                industryMap.put(industry.getId(), industry.getName());
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        try {
            create(list);
        } catch (Exception e) {
            log.error("init error!", e);
            throw e;
        }
    }

    public Page<ProjectSimpleInfo> search(ProjectCondition condition) throws IOException, ParseException {
        BooleanQuery rootQuery = new BooleanQuery();
        if (condition.getMinPay() != null && condition.getMaxPay() != null) {
            rootQuery.add(new QueryParser("agentId", analyzer).parse(String.valueOf(condition.getAgentId())),
                    BooleanClause.Occur.MUST);
        }
        if (condition.getAgentId() != null) {
            rootQuery.add(new QueryParser("agentId", analyzer).parse(String.valueOf(condition.getAgentId())),
                    BooleanClause.Occur.MUST);
        }
        if (condition.getFreeze() != null) {
            rootQuery.add(new QueryParser("freeze", analyzer).parse(condition.getFreeze() ? "1" : "0"), BooleanClause
                    .Occur.MUST);
        }
        if (condition.getKeyword() != null) {
            rootQuery.add(new QueryParser("name", analyzer).parse(condition.getKeyword()), BooleanClause.Occur.MUST);
        }
        if (condition.getManagerId() != null) {
            rootQuery.add(new QueryParser("managerId", analyzer).parse(String.valueOf(condition.getManagerId())), BooleanClause
                    .Occur.MUST);
        }
        if (condition.getCity() != null) {
            BooleanQuery cityQuery = new BooleanQuery();
            for (Integer i : condition.getCity()) {
                cityQuery.add(new QueryParser("city", analyzer).parse(String.valueOf(i)), BooleanClause.Occur.SHOULD);
            }
            rootQuery.add(cityQuery, BooleanClause.Occur.MUST);
        }
        if (condition.getIndustry() != null) {
            BooleanQuery industryQuery = new BooleanQuery();
            for (Integer i : condition.getIndustry()) {
                industryQuery.add(new QueryParser("industry", analyzer).parse(String.valueOf(i)), BooleanClause.Occur
                        .SHOULD);
            }
            rootQuery.add(industryQuery, BooleanClause.Occur.MUST);
        }
        if (condition.getSkill() != null) {
            BooleanQuery skillQuery = new BooleanQuery();
            for (Integer i : condition.getSkill()) {
                skillQuery.add(new QueryParser("skill", analyzer).parse(String.valueOf(i)), BooleanClause.Occur
                        .SHOULD);
            }
            rootQuery.add(skillQuery, BooleanClause.Occur.MUST);
        }
        if (condition.getStatus() != null) {
            BooleanQuery statusQuery = new BooleanQuery();
            for (Integer i : condition.getStatus()) {
                statusQuery.add(new QueryParser("industry", analyzer).parse(String.valueOf(i)), BooleanClause.Occur
                        .SHOULD);
            }
            rootQuery.add(statusQuery, BooleanClause.Occur.MUST);
        }
        int hitsPerPage = condition.getPageSize();
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, null);
        searcher.search(rootQuery, collector);
        TopDocs topDocs = collector.topDocs(condition.getOffset(), condition.getPageSize());
        ScoreDoc[] hits = topDocs.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");
        List<ProjectSimpleInfo> list = new ArrayList<ProjectSimpleInfo>();
        for (int i = 0; i < hits.length; ++i) {
            list.add(buildInfo(searcher.doc(hits[i].doc)));
        }
        return new Page<ProjectSimpleInfo>(list, condition.getOffset() / condition.getPageSize
                () + 1, condition.getPageSize(), topDocs.totalHits);
    }

    private ProjectSimpleInfo buildInfo(Document d) {
        return new Gson().fromJson(d.get("json"), ProjectSimpleInfo.class);
    }

    private void create(List<Project> list) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        for (Project p : list) {
            addDoc(w, p, skillMap.get(p.getId()));
        }
        w.close();
    }

    public void update(Project p) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        w.updateDocument(new Term("id", String.valueOf(p.getId())), buildDoc(p, projectSkillDao.selectByProjectId(p.getId())));
        w.close();
    }

    public void add(Project p) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        addDoc(w, p, projectSkillDao.selectByProjectId(p.getId()));
        w.close();
    }

    private void addDoc(IndexWriter w, Project p, List<ProjectSkill> skills) throws IOException {
        log.info("write doc json:" + toJson(p));
        w.addDocument(buildDoc(p, skills));
    }

    private Document buildDoc(Project p, List<ProjectSkill> skills) {
        Document doc = new Document();
        doc.add(new StringField("id", String.valueOf(p.getId()), Field.Store.NO));
        doc.add(new TextField("name", p.getName(), Field.Store.NO));
        doc.add(new StringField("managerId", String.valueOf(p.getManagerId()), Field.Store.NO));
        doc.add(new StringField("agentId", String.valueOf(p.getAgentId()), Field.Store.NO));
        doc.add(new StringField("minPay", String.valueOf(p.getMinPay()), Field.Store.NO));
        doc.add(new StringField("maxPay", String.valueOf(p.getMaxPay()), Field.Store.NO));
        doc.add(new StringField("resident", p.getResident() ? "1" : "0", Field.Store.NO));
        doc.add(new StringField("status", String.valueOf(p.getStatus().intValue()), Field.Store.NO));
        doc.add(new StringField("city", String.valueOf(p.getCity()), Field.Store.NO));
        doc.add(new StringField("industry", String.valueOf(p.getIndustry()), Field.Store.NO));
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isNotEmpty(skills)) {
            for (ProjectSkill skill : skills) {
                sb.append(skill.getSkillId()).append(" ");
            }
        }
        doc.add(new TextField("skill", sb.toString(), Field.Store.NO));
        doc.add(new StringField("freeze", p.getFreeze() ? "1" : "0", Field.Store.NO));
        doc.add(new StringField("json", toJson(p), Field.Store.YES));
        return doc;
    }

    private String toJson(Project p) {
        ProjectSimpleInfo info = ProjectSimpleInfo.parse(p);
        info.setName(p.getName());
        info.setId(p.getId());
        info.setImage(p.getImage());
        info.setDesc(p.getDesc());
        info.setStartDate(DateUtils.format(p.getAddTime(), "yyyy-MM-dd"));
        info.setStatus(p.getStatus().intValue());
        info.setCity(new KVInfo(p.getCity(), cityMap.get(p.getCity())));
        info.setIndustry(new KVInfo(p.getIndustry(), industryMap.get(p.getIndustry())));
        if (p.getManagerId() != null && !p.getManagerId().equals(0L)) {
            info.setManager(new KVLongInfo(p.getManagerId(), ""));
        }
        if (p.getAgentId() != null && !p.getAgentId().equals(0L)) {
            info.setBroker(new KVLongInfo(p.getAgentId(), ""));
        }
        return new Gson().toJson(info);
    }
}
