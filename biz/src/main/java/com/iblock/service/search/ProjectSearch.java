package com.iblock.service.search;

import com.google.gson.Gson;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.utils.DateUtils;
import com.iblock.dao.IndustryDao;
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
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
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
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
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
    @Resource
    private IndustryDao industryDao;

    private IndexWriter indexWriter;

    private Map<Integer, String> cityMap;
    private Map<Integer, String> industryMap;
    private Map<Long, ArrayList<ProjectSkill>> skillMap;

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private Directory index = new RAMDirectory();

    public void refreshCity() {
        List<City> cities = metaService.getCities(null);
        cityMap = new HashMap<Integer, String>();
        if (CollectionUtils.isNotEmpty(cities)) {
            for (City city : cities) {
                cityMap.put(city.getCityId(), city.getCityName());
            }
        }
    }

    public void refreshIndustry() {
        List<Industry> industries = industryDao.selectAll();
        industryMap = new HashMap<Integer, String>();
        if (CollectionUtils.isNotEmpty(industries)) {
            for (Industry industry : industries) {
                industryMap.put(industry.getId(), industry.getName());
            }
        }
    }

    public void refreshSkill() {
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
    }

    @PostConstruct
    public void init() throws Exception {
        ProjectSearchBean bean = new ProjectSearchBean();
        bean.setOffset(0);
        bean.setPageSize(100000000);
        List<Project> list = projectDao.list(bean);

        refreshSkill();
        refreshIndustry();
        refreshCity();
        try {
            create(list);
        } catch (Exception e) {
            log.error("init error!", e);
            throw e;
        }
    }

    public Page<ProjectSimpleInfo> search(ProjectCondition c) throws IOException, ParseException {
        BooleanQuery rootQuery = new BooleanQuery();
        if (c.getMinPay() != null || c.getMaxPay() != null) {
            BooleanQuery payQuery = new BooleanQuery();
            payQuery.add(NumericRangeQuery.newIntRange("maxPay", c.getMinPay(), c.getMaxPay(), true, true),
                    BooleanClause.Occur.SHOULD);
            payQuery.add(NumericRangeQuery.newIntRange("minPay", c.getMinPay(), c.getMaxPay(), true, true),
                    BooleanClause.Occur.SHOULD);
            rootQuery.add(payQuery, BooleanClause.Occur.MUST);
        }
        if (c.getAgentId() != null) {
            rootQuery.add(new QueryParser("agentId", analyzer).parse(String.valueOf(c.getAgentId())),
                    BooleanClause.Occur.MUST);
        }
        if (c.getFreeze() != null) {
            rootQuery.add(new QueryParser("freeze", analyzer).parse(c.getFreeze() ? "1" : "0"), BooleanClause
                    .Occur.MUST);
        }
        if (c.getKeyword() != null) {
            rootQuery.add(new QueryParser("name", analyzer).parse(c.getKeyword()), BooleanClause.Occur.MUST);
        }
        if (c.getManagerId() != null) {
            rootQuery.add(new QueryParser("managerId", analyzer).parse(String.valueOf(c.getManagerId())), BooleanClause
                    .Occur.MUST);
        }
        if (c.getCity() != null) {
            BooleanQuery cityQuery = new BooleanQuery();
            for (Integer i : c.getCity()) {
                cityQuery.add(new QueryParser("city", analyzer).parse(String.valueOf(i)), BooleanClause.Occur.SHOULD);
            }
            rootQuery.add(cityQuery, BooleanClause.Occur.MUST);
        }
        if (c.getIndustry() != null) {
            BooleanQuery industryQuery = new BooleanQuery();
            for (Integer i : c.getIndustry()) {
                industryQuery.add(new QueryParser("industry", analyzer).parse(String.valueOf(i)), BooleanClause.Occur
                        .SHOULD);
            }
            rootQuery.add(industryQuery, BooleanClause.Occur.MUST);
        }
        if (c.getSkill() != null) {
            BooleanQuery skillQuery = new BooleanQuery();
            for (Integer i : c.getSkill()) {
                skillQuery.add(new QueryParser("skill", analyzer).parse(String.valueOf(i)), BooleanClause.Occur
                        .SHOULD);
            }
            rootQuery.add(skillQuery, BooleanClause.Occur.MUST);
        }
        if (c.getStatus() != null) {
            BooleanQuery statusQuery = new BooleanQuery();
            for (Integer i : c.getStatus()) {
                statusQuery.add(new QueryParser("status", analyzer).parse(String.valueOf(i)), BooleanClause.Occur
                        .SHOULD);
            }
            rootQuery.add(statusQuery, BooleanClause.Occur.MUST);
        }
        if (c.getIds() != null) {
            BooleanQuery idQuery = new BooleanQuery();
            for (Long i : c.getIds()) {
                idQuery.add(new QueryParser("id", analyzer).parse(String.valueOf(i)), BooleanClause.Occur.SHOULD);
            }
            rootQuery.add(idQuery, BooleanClause.Occur.MUST);
        }
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        Sort sort = new Sort(SortField.FIELD_SCORE, new SortField("addTime", SortField.Type.LONG, true));
        TopFieldCollector collector = TopFieldCollector.create(sort, c.getOffset() + c.getPageSize(), false, false, false);

        searcher.search(rootQuery, collector);
        TopDocs topDocs = collector.topDocs(c.getOffset(), c.getPageSize());
        ScoreDoc[] hits = topDocs.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");
        List<ProjectSimpleInfo> list = new ArrayList<ProjectSimpleInfo>();
        for (int i = 0; i < hits.length; ++i) {
            list.add(buildInfo(searcher.doc(hits[i].doc)));
        }
        return new Page<ProjectSimpleInfo>(list, c.getOffset() / c.getPageSize
                () + 1, c.getPageSize(), topDocs.totalHits);
    }

    private ProjectSimpleInfo buildInfo(Document d) {
        return new Gson().fromJson(d.get("json"), ProjectSimpleInfo.class);
    }

    private void create(List<Project> list) throws IOException {
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(index, config);
            if (CollectionUtils.isNotEmpty(list)) {
                for (Project p : list) {
                    addDoc(indexWriter, p, skillMap.get(p.getId()));
                }
            }
        } finally {
            if (indexWriter != null) {
                indexWriter.close();
            }
        }

    }

    public void update(Project p) throws IOException {
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(index, config);
            indexWriter.updateDocument(new Term("id", String.valueOf(p.getId())), buildDoc(p, projectSkillDao.selectByProjectId(p.getId())));
            indexWriter.close();
        } finally {
            if (indexWriter != null) {
                indexWriter.close();
            }
        }

    }

    public void add(Project p) throws IOException {
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(index, config);
            addDoc(indexWriter, p, projectSkillDao.selectByProjectId(p.getId()));
        } finally {
            if (indexWriter != null) {
                indexWriter.close();
            }
        }
    }

    private void addDoc(IndexWriter w, Project p, List<ProjectSkill> skills) throws IOException {
        log.info("write doc json:" + toJson(p));
        w.addDocument(buildDoc(p, skills));
    }

    private Document buildDoc(Project p, List<ProjectSkill> skills) {
        Document doc = new Document();
        doc.add(new StringField("id", String.valueOf(p.getId()), Field.Store.NO));
        doc.add(new TextField("name", p.getName(), Field.Store.NO));
        doc.add(new StringField("managerId", p.getManagerId() == null ? "" : String.valueOf(p.getManagerId()), Field
                .Store.NO));
        doc.add(new StringField("agentId", p.getAgentId() == null ? "" : String.valueOf(p.getAgentId()), Field.Store
                .NO));
        doc.add(new IntField("minPay", p.getMinPay(), Field.Store.NO));
        doc.add(new IntField("maxPay", p.getMaxPay(), Field.Store.NO));
        doc.add(new StringField("resident", p.getResident() == null ? "0" : p.getResident() ? "1" : "0", Field.Store
                .NO));
        doc.add(new StringField("status", String.valueOf(p.getStatus().intValue()), Field.Store.NO));
        doc.add(new StringField("city", String.valueOf(p.getCity()), Field.Store.NO));
        doc.add(new LongField("addTime", p.getAddTime().getTime(), Field.Store.YES));
        doc.add(new NumericDocValuesField("addTime", p.getAddTime().getTime()));
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
