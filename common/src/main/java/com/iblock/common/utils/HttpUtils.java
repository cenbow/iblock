package com.iblock.common.utils;

import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuqihong on 15/7/1.
 */
@Log4j
public class HttpUtils {

    public static String post(String url, Map<String, String> entity) {
        StringBuffer sb = new StringBuffer();
        if (MapUtils.isNotEmpty(entity)) {
            Set<String> keySet = entity.keySet();
            for (String s : keySet) {
                sb.append(s).append("=").append(entity.get(s)).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return post(url, sb.toString());
    }

    public static String get(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resp = "";
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            log.info("executing request " + httpGet.getRequestLine());
            response = httpclient.execute(httpGet);
            log.info("----------------------------------------");
            log.info(response.getStatusLine());
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                log.info("Response content length: "
                        + resEntity.getContentLength());
                resp = EntityUtils.toString(resEntity, "UTF-8");
            }
        } catch (ClientProtocolException e) {
            log.error("http get error", e);
        } catch (IOException e) {
            log.error("http get error", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("http get close client error", e);
            }
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("http get close resp error", e);
                }
            }
        }
        log.info(resp);
        return resp;
    }

    private static String post(String url, String entity) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resp = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost httppost = new HttpPost(url);
            StringEntity strBody = new StringEntity(entity, ContentType.create("text/plain", Consts.UTF_8));
            log.info("http util request:" + entity);
            httppost.setEntity(strBody);
            log.info("executing request " + httppost.getRequestLine());
            response = httpclient.execute(httppost);
            log.info("----------------------------------------");
            log.info(response.getStatusLine());
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                log.info("Response content length: "
                        + resEntity.getContentLength());
                resp = EntityUtils.toString(resEntity, "UTF-8");
            }
        } catch (ClientProtocolException e) {
            log.error("http post error", e);
        } catch (IOException e) {
            log.error("http post error", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("http post close client error", e);
            }
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("http post close resp error", e);
                }
            }
        }
        log.info(resp);
        return resp;
    }
}
