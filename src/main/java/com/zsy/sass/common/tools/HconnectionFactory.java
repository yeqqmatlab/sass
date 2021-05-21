package com.zsy.sass.common.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class HconnectionFactory implements InitializingBean {

    private static final Log log = LogFactory.getLog(HconnectionFactory.class);

    @Value("${hbase.zookeeper.quorum}")
    private String zkQuorum;

    @Value("${hbase.master}")
    private String hBaseMaster;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String zkPort;

    @Value("${zookeeper.znode.parent}")
    private String znode;

    private static Configuration conf = HBaseConfiguration.create();

    public static Connection connection;

    @Override
    public void afterPropertiesSet() throws Exception {

        conf.set("hbase.zookeeper.quorum", zkQuorum);
        conf.set("hbase.zookeeper.property.clientPort",zkPort);
        conf.set("zookeeper.znode.parent",znode);
        conf.set("hbase.master",hBaseMaster);

        try {
            connection = ConnectionFactory.createConnection(conf);
            log.info("获取connectiont连接成功！");
        } catch (IOException e) {
            log.error("获取connectiont连接失败！", e);
            e.printStackTrace();

        }


    }
}
