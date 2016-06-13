package com.iblock.service.message;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by baidu on 16/6/13.
 */
@Slf4j
public class MsgContentUtil {

    private Map<Integer, Msg> map = new HashMap<Integer, Msg>();

    private static MsgContentUtil instance = new MsgContentUtil();

    private MsgContentUtil() {
        try {
            initConfig();
        } catch (Exception e) {
            log.error("MsgContentUtil.initConfig error!", e);
        }
    }

    public static MsgContentUtil getInstance() {
        return instance;
    }

    private void initConfig() throws DocumentException, FileNotFoundException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(this.getClass().getClassLoader()
                .getResourceAsStream("msg.xml"));
        Element root = document.getRootElement();
        List<Element> modules = root.elements("message");
        for (Element ele : modules) {
            Msg msg = new Msg();
            msg.setAction(Integer.parseInt(ele.attributeValue("action")));
            msg.setType(Integer.parseInt(ele.attributeValue("type")));
            msg.setContent(ele.attributeValue("detail"));
            msg.setService(ele.attributeValue("service"));
            Element paramsRoot = ele.element("params");
            if (paramsRoot != null) {
                List<Element> params = paramsRoot.elements("param");
                Set<String> paramSet = new HashSet<String>();
                for (Element param : params) {
                    paramSet.add(param.attributeValue("name"));
                }
                msg.setParams(paramSet);
            }
            Element inputRoot = ele.element("inputs");
            if (inputRoot != null) {
                List<Element> inputList = inputRoot.elements("input");
                Map<String, String> inputMap = new HashMap<String, String>();
                for (Element input : inputList) {
                    inputMap.put(input.attributeValue("name"), input.attributeValue("type"));
                }
                msg.setInputType(inputMap);
            }
            map.put(msg.getAction(), msg);
        }
    }

    public Msg getMsg(int action) {
        return map.get(action);
    }
}
