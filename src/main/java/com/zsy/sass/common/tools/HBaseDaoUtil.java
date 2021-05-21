package com.zsy.sass.common.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * created by yqq
 * date 2021-5-20
 *  HbaseDao 操作工具类
 */
@Component("hBaseDaoUtil")
public class HBaseDaoUtil {

    private static final Log log = LogFactory.getLog(HBaseDaoUtil.class);

    /**
     * 关闭连接
     */
    public static void close(){
        if (HconnectionFactory.connection != null) {
            try {
                HconnectionFactory.connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建表
     */
    public void createTable(String tableName, Set<String> familyColumn){
        TableName tn = TableName.valueOf(tableName);
        try {
            Admin admin = HconnectionFactory.connection.getAdmin();
            HTableDescriptor htd = new HTableDescriptor(tn);
            for (String fc : familyColumn) {
                HColumnDescriptor hcd = new HColumnDescriptor(fc);
                htd.addFamily(hcd);
            }
            admin.createTable(htd);
        } catch (IOException e) {
            log.error("创建"+tableName+"表失败！", e);
            e.printStackTrace();
        }

    }

    /**
     * find by rowkey
     */
    public <T> List<T> get(T obj, String ... rowKeys){
        List<T> list = new ArrayList<>();
        String tableName = getORMTable(obj);
        if (StringUtils.isBlank(tableName)) {
            return list;
        }
        try {
            //Table table = HconnectionFactory.connection.getTable(TableName.valueOf(tableName));
            Admin admin = HconnectionFactory.connection.getAdmin();
            if (!admin.isTableAvailable(TableName.valueOf(tableName))) {
                return list;
            }
            List<Result> results = getResults(tableName, rowKeys);
            if (results.isEmpty()) {
                return list;
            }
            for (int i = 0; i < results.size(); i++) {
                T bean = null;
                Result result = results.get(i);
                if (result == null || result.isEmpty()) {
                    continue;
                }
                try {
                    bean = HBaseBeanUtil.resultToBean(result, obj);
                    list.add(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("查询异常", e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    // 获取tableName
    private String getORMTable(Object obj) {
        HbaseTable table = obj.getClass().getAnnotation(HbaseTable.class);
        return table.tableName();
    }

    // 获取查询结果
    private List<Result> getResults(String tableName, String ... rowKeys){
        List<Result> resultList = new ArrayList<>();
        List<Get> gets = new ArrayList<>();
        for (String rowKey : rowKeys) {
            if (StringUtils.isBlank(rowKey)) {
                continue;
            }
            Get get = new Get(Bytes.toBytes(rowKey));
            gets.add(get);

        }
        try {
            Table table = HconnectionFactory.connection.getTable(TableName.valueOf(tableName));
            Result[] results = table.get(gets);
            Collections.addAll(resultList, results);
            return resultList;
        } catch (IOException e) {
            e.printStackTrace();
            return resultList;
        }


    }




















}
