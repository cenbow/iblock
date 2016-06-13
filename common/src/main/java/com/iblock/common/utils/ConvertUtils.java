package com.iblock.common.utils;

import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by baidu on 16/6/10.
 */
public class ConvertUtils {

    /**
     * 复制对象
     * @param object
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T copy(T object) throws Exception {
        return (T) copy(object, object.getClass());
    }

    /**
     * 将某个对象的同名属性复制到另一个对象中
     * @param object
     * @param retClassType
     * @param <T1>
     * @param <T0>
     * @return
     * @throws Exception
     */
    public static <T1, T0> T0 copy(T1 object, Class<T0> retClassType) throws Exception {
        Class classType = object.getClass();
        Constructor constructor = retClassType.getConstructor(new Class[0]);
        Object objectCopy = constructor.newInstance(new Object[0]);
        Field[] fields = (Field[]) ArrayUtils.addAll(classType.getDeclaredFields(),
                classType.getSuperclass().getDeclaredFields());
        for (int i = 0; i < fields.length; ++i) {
            try {
                Field field = fields[i];
                String fieldName = field.getName();
                String firstLetter = fieldName.substring(0, 1).toUpperCase();
                String getMethodName = "get" + firstLetter + fieldName.substring(1);
                String setMethodName = "set" + firstLetter + fieldName.substring(1);
                Method getMethod = classType.getMethod(getMethodName, new Class[0]);
                Method setMethod = retClassType.getMethod(setMethodName, new Class[]{field.getType()});
                Object value = getMethod.invoke(object, new Object[0]);
                setMethod.invoke(objectCopy, new Object[]{value});
            } catch (Exception e) {
                // 如果找不到目标对象的SET方法则跳过
            }
        }

        return (T0) objectCopy;
    }
}
