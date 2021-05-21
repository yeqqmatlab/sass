package com.zsy.sass.common.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * javabean <--> HBaseBean
 */
public class HBaseBeanUtil {

    private static final Log log = LogFactory.getLog(HBaseBeanUtil.class);


    /**
     * HBase --> bean
     * @param result
     * @param obj
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T resultToBean(Result result, T obj) throws Exception {

        if (null == result) {
            return null;
        }

        Class<?> clazz = obj.getClass();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(HbaseColumn.class)) {
                continue;
            }
            HbaseColumn orm = field.getAnnotation(HbaseColumn.class);
            String family = orm.family();
            String qualifier = orm.qualifier();
            boolean timestamp = orm.timestamp();
            if (StringUtils.isBlank(family) || StringUtils.isBlank(qualifier)) {
                continue;
            }
            String fieldName = field.getName();
            String value = "";
            if ("rowkey".equalsIgnoreCase(family)) {
                value = new String(result.getRow());
            }else{
                value = getResultValueByType(result, family, qualifier, timestamp);
            }

            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setMethodName = "set" + firstLetter + fieldName.substring(1);
            Method setMethod = clazz.getMethod(setMethodName, new Class[]{field.getType()});
            setMethod.invoke(obj, new Object[]{value});
        }
        return obj;
    }

    /**
     * @param result
     * @param family
     * @param qualifier
     * @param timeStamp
     * @return
     */
    private static String getResultValueByType(Result result, String family, String qualifier, boolean timeStamp) {
        if (!timeStamp) {
            return new String(result.getValue(Bytes.toBytes(family), Bytes.toBytes(qualifier)));
        }
        List<Cell> cells = result.getColumnCells(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        if (cells.size() == 1) {
            Cell cell = cells.get(0);
            return cell.getTimestamp() + "";
        }
        return "";
    }
}



