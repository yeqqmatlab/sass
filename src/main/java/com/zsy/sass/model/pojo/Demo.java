package com.zsy.sass.model.pojo;

import com.zsy.sass.common.tools.HbaseColumn;
import com.zsy.sass.common.tools.HbaseTable;

@HbaseTable(tableName="t_demo")
public class Demo {

    @HbaseColumn(family="rowkey", qualifier="rowkey")
    private String id;

    @HbaseColumn(family="demo", qualifier="content")
    private String content;

    @HbaseColumn(family="demo", qualifier="avg")
    private String avg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

}
