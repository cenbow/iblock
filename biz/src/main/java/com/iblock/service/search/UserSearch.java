package com.iblock.service.search;

import com.google.gson.Gson;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.UserRole;
import com.iblock.dao.IndustryDao;
import com.iblock.dao.JobInterestDao;
import com.iblock.dao.SkillDao;
import com.iblock.dao.UserDao;
import com.iblock.dao.po.City;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.JobInterest;
import com.iblock.dao.po.Skill;
import com.iblock.dao.po.User;
import com.iblock.service.info.KVInfo;
import com.iblock.service.info.UserSearchInfo;
import com.iblock.service.meta.MetaService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
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
 * Created by baidu on 16/7/24.
 */
@Component
@Log4j
public class UserSearch {

    @Resource
    private UserDao userDao;
    @Resource
    private JobInterestDao jobInterestDao;
    @Resource
    private MetaService metaService;
    @Resource
    private IndustryDao industryDao;
    @Resource
    private SkillDao skillDao;

    private Map<Integer, String> cityMap;
    private Map<Integer, String> industryMap;
    private Map<Long, JobInterest> interestMap;
    private Map<Integer, String> skillMap;

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private Directory index = new RAMDirectory();
    private IndexWriter indexWriter;

    public void refreshCity() {
        List<City> cities = metaService.getCities(null);
        cityMap = new HashMap<Integer, String>();
        if (CollectionUtils.isNotEmpty(cities)) {
            for (City city : cities) {
                cityMap.put(city.getCityId(), city.getCityName());
            }
        }
    }

    public void refreshInterest() {
        List<JobInterest> interests = jobInterestDao.selectAll();
        interestMap = new HashMap<Long, JobInterest>();
        if (CollectionUtils.isNotEmpty(interests)) {
            for (JobInterest interest : interests) {
                interestMap.put(interest.getUserId(), interest);
            }
        }
    }

    public void refreshSkill() {
        skillMap = new HashMap<Integer, String>();
        List<Skill> skills = skillDao.selectAll();
        if (CollectionUtils.isNotEmpty(skills)) {
            for (Skill skill : skills) {
                skillMap.put(skill.getId(), skill.getName());
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

    @PostConstruct
    public void init() throws Exception {
        ProjectSearchBean bean = new ProjectSearchBean();
        bean.setOffset(0);
        bean.setPageSize(100000000);
        List<User> list = userDao.selectByStatus(null);
        refreshCity();
        refreshIndustry();
        refreshInterest();
        refreshSkill();

        try {
            create(list);
        } catch (Exception e) {
            log.error("init error!", e);
            throw e;
        }
    }

    public Page<UserSearchInfo> search(UserCondition c) throws IOException, ParseException {
        BooleanQuery rootQuery = new BooleanQuery();
        rootQuery.add(new QueryParser("online", analyzer).parse("1"), BooleanClause.Occur.MUST);
        if (c.getMinPay() != null || c.getMaxPay() != null) {
            BooleanQuery payQuery = new BooleanQuery();
            payQuery.add(NumericRangeQuery.newIntRange("maxPay", c.getMinPay(), c.getMaxPay(), true, true),
                    BooleanClause.Occur.SHOULD);
            payQuery.add(NumericRangeQuery.newIntRange("minPay", c.getMinPay(), c.getMaxPay(), true, true),
                    BooleanClause.Occur.SHOULD);
            rootQuery.add(payQuery, BooleanClause.Occur.MUST);
        }
        if (c.getKeyword() != null) {
            rootQuery.add(new QueryParser("name", analyzer).parse(c.getKeyword()), BooleanClause.Occur.MUST);
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
        Sort sort = new Sort(new SortField("addTime", SortField.Type.LONG, true));
        TopFieldCollector collector = TopFieldCollector.create(sort, c.getOffset() + c.getPageSize(), false, false, false);

        searcher.search(rootQuery, collector);
        TopDocs topDocs = collector.topDocs(c.getOffset(), c.getPageSize());
        ScoreDoc[] hits = topDocs.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");
        List<UserSearchInfo> list = new ArrayList<UserSearchInfo>();
        for (int i = 0; i < hits.length; ++i) {
            list.add(buildInfo(searcher.doc(hits[i].doc)));
        }
        return new Page<UserSearchInfo>(list, c.getOffset() / c.getPageSize
                () + 1, c.getPageSize(), topDocs.totalHits);
    }

    private UserSearchInfo buildInfo(Document d) {
        return new Gson().fromJson(d.get("json"), UserSearchInfo.class);
    }

    private void create(List<User> list) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        indexWriter = new IndexWriter(index, config);
        if (CollectionUtils.isNotEmpty(list)) {
            for (User u : list) {
                if (u.getRole().intValue() != UserRole.DESIGNER.getRole()) {
                    continue;
                }
                addDoc(indexWriter, u, interestMap.get(u.getId()));
            }
        }
        indexWriter.close();
    }

    public void update(User u) throws IOException {
        if (u.getRole().intValue() != UserRole.DESIGNER.getRole()) {
            return;
        }
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        indexWriter = new IndexWriter(index, config);
        indexWriter.updateDocument(new Term("id", String.valueOf(u.getId())), buildDoc(u, jobInterestDao.selectByUser(u.getId())));
        indexWriter.close();
    }

    public void add(User u) throws IOException {
        if (u.getRole().intValue() != UserRole.DESIGNER.getRole()) {
            return;
        }
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        indexWriter = new IndexWriter(index, config);
        addDoc(indexWriter, u, jobInterestDao.selectByUser(u.getId()));
        indexWriter.close();
    }

    private void addDoc(IndexWriter w, User u, JobInterest interest) throws IOException {
        log.info("write doc json:" + toJson(u, interest));
        w.addDocument(buildDoc(u, interest));
    }

    private Document buildDoc(User u, JobInterest i) {
        Document doc = new Document();
        doc.add(new StringField("id", String.valueOf(u.getId()), Field.Store.NO));
        doc.add(new StringField("online", (u.getOnline() != null && u.getOnline()) ? "1" : "0", Field.Store.NO));
        doc.add(new TextField("name", u.getUserName(), Field.Store.NO));
        doc.add(new IntField("minPay", i == null ? -1 : i.getStartPay(), Field.Store.NO));
        doc.add(new IntField("maxPay", i == null ? -1 : i.getEndPay(), Field.Store.NO));
        doc.add(new StringField("status", String.valueOf(u.getStatus().intValue()), Field.Store.NO));
        doc.add(new TextField("city", i == null ? "" : i.getCityList().replaceAll(",", " "), Field.Store.NO));
        doc.add(new LongField("addTime", u.getAddTime().getTime(), Field.Store.YES));
        doc.add(new NumericDocValuesField("addTime", u.getAddTime().getTime()));
        doc.add(new TextField("industry", i == null ? "" : i.getJobTypeList().replaceAll(",", " "), Field.Store.NO));
        doc.add(new TextField("skill", StringUtils.isBlank(u.getSkills()) ? "" : u.getSkills().replaceAll(",", " "),
                Field.Store.NO));
        doc.add(new StringField("json", toJson(u, i), Field.Store.YES));
        return doc;
    }

    private String toJson(User u, JobInterest interest) {
        UserSearchInfo info = new UserSearchInfo();
        info.setName(u.getUserName());
        info.setId(u.getId());
        info.setAvatar(u.getHeadFigure());
        if (u.getSex() != null) {
            info.setGender(u.getSex() ? 1 : 2);
        }
        info.setRole(u.getRole().intValue());
        List<KVInfo> cityList = new ArrayList<KVInfo>();
        if (interest != null && StringUtils.isNotBlank(interest.getCityList())) {
            for (String s : interest.getCityList().split(",")) {
                if (StringUtils.isBlank(s)) {
                    continue;
                }
                cityList.add(new KVInfo(Integer.parseInt(s), cityMap.get(Integer.parseInt(s))));
            }
        }
        info.setCity(cityList);
        List<KVInfo> industryList = new ArrayList<KVInfo>();
        if (interest != null && StringUtils.isNotBlank(interest.getJobTypeList())) {
            for (String s : interest.getJobTypeList().split(",")) {
                if (StringUtils.isBlank(s)) {
                    continue;
                }
                industryList.add(new KVInfo(Integer.parseInt(s), industryMap.get(Integer.parseInt(s))));
            }
        }
        info.setIndustry(industryList);
        List<KVInfo> skillList = new ArrayList<KVInfo>();
        if (StringUtils.isNotBlank(u.getSkills())) {
            for (String s : u.getSkills().split(",")) {
                if (StringUtils.isBlank(s)) {
                    continue;
                }
                skillList.add(new KVInfo(Integer.parseInt(s), skillMap.get(Integer.parseInt(s))));
            }
        }
        info.setSkill(skillList);
        return new Gson().toJson(info);
    }
}
