package com.iblock.service.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by baidu on 16/6/10.
 */
@Service("redisUtils")
public class RedisUtils {

    private Logger logger = Logger.getLogger(RedisUtils.class);

    @Value("${redis.cache.time}")
    private Long time;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    protected ThreadPoolTaskExecutor taskExecutor;

    public <T> T fetch(String key) {
        try {
            ValueOperations<String, T> tmp = redisTemplate.opsForValue();
            return tmp.get(key);
        } catch (Exception e) {
            logger.error("fetch redis cache error", e);
        }
        return null;
    }

    public <T> void put(String key, T obj) {
        try {
            redisTemplate.opsForValue().set(key, obj, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("insert redis cache error", e);
        }
    }

    public <T> void put(String key, T obj, long defTime) {
        if (defTime <= 0) {
            defTime = 30;
        }
        try {
            redisTemplate.opsForValue().set(key, obj, defTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("insert redis cache error", e);
        }
    }

    public <T> List<T> multiGet(List<String> keys) {
        try {
            ValueOperations<String, T> tmp = redisTemplate.opsForValue();
            List<T> list = tmp.multiGet(keys);
            list.removeAll(Collections.singleton(null));
            return list;
        } catch (Exception e) {
            logger.error("multi get redis cache error", e);
        }
        return new ArrayList<T>();
    }

    public boolean rm(String... keys) {
        try {
            redisTemplate.delete(Arrays.asList(keys));
            return true;
        } catch (Exception e) {
            logger.error("rm redis cache error", e);
        }
        return false;
    }
}
