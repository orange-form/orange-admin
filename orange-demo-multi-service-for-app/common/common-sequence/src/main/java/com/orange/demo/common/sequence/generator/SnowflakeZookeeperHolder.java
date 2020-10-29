package com.orange.demo.common.sequence.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Snowflake Id生成器所依赖的zk工具类。该实现完全copy美团的leaf。
 *
 * @author MeiTuan.Team
 * @date 2020-08-08
 */
@Slf4j
@Data
public class SnowflakeZookeeperHolder {
    private static final int CONNECTION_TIMEOUT_MS = 10000;
    private static final int SESSION_TIMEOUT_MS = 6000;
    private String zkAddressNode = null;
    private String listenAddress;
    private int workerId;
    private String ip;
    private String port;
    private String connectionString;
    private String pathForever;
    private final String cachePath;
    private long lastUpdateTime;

    public SnowflakeZookeeperHolder(String ip, String port, String connectionString, String zkPath) {
        this.ip = ip;
        this.port = port;
        this.listenAddress = ip + ":" + port;
        this.connectionString = connectionString;
        this.pathForever = "/snowflake/" + zkPath + "/forever";
        this.cachePath = System.getProperty("java.io.tmpdir")
                + File.separator + zkPath + "/leafconf/{port}/workerID.properties";
    }

    /**
     * 初始化zk中的持久化SEQUENTIAL节点数据。如果不存在就创建新的，存在则引用原有的。
     *
     * @return true初始化成功，否则失败。
     */
    public boolean init() {
        try {
            CuratorFramework curator = createWithOptions(connectionString,
                    new RetryUntilElapsed(1000, 4));
            curator.start();
            Stat stat = curator.checkExists().forPath(pathForever);
            if (stat == null) {
                //不存在根节点,机器第一次启动,创建/snowflake/ip:port-000000000,并上传数据
                zkAddressNode = createNode(curator);
                //worker id 默认是0
                updateLocalWorkerId(workerId);
                //定时上报本机时间给forever节点
                scheduledUploadData(curator, zkAddressNode);
                return true;
            } else {
                //ip:port->00001
                Map<String, Integer> nodeMap = Maps.newHashMap();
                //ip:port->(ipport-000001)
                Map<String, String> realNode = Maps.newHashMap();
                //存在根节点,先检查是否有属于自己的根节点
                List<String> keys = curator.getChildren().forPath(pathForever);
                for (String key : keys) {
                    String[] nodeKey = key.split("-");
                    realNode.put(nodeKey[0], key);
                    nodeMap.put(nodeKey[0], Integer.parseInt(nodeKey[1]));
                }
                Integer workerid = nodeMap.get(listenAddress);
                if (workerid != null) {
                    //有自己的节点,zk_AddressNode=ip:port
                    zkAddressNode = pathForever + "/" + realNode.get(listenAddress);
                    //启动worder时使用会使用
                    workerId = workerid;
                    if (!checkInitTimeStamp(curator, zkAddressNode)) {
                        throw new CheckLastTimeException(
                                "Init timestamp check error,forever node timestamp greater than this node time");
                    }
                } else {
                    //表示新启动的节点,创建持久节点 ,不用check时间
                    String newNode = createNode(curator);
                    zkAddressNode = newNode;
                    String[] nodeKey = newNode.split("-");
                    workerId = Integer.parseInt(nodeKey[1]);
                }
                doService(curator);
                updateLocalWorkerId(workerId);
            }
        } catch (Exception e) {
            log.error("Start node ERROR", e);
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(new File(cachePath.replace("{port}", port + ""))));
                workerId = Integer.parseInt(properties.getProperty("workerId"));
                log.warn("START FAILED ,use local node file properties workerID-{}", workerId);
            } catch (Exception e1) {
                log.error("Read file error ", e1);
                return false;
            }
        }
        return true;
    }

    private void doService(CuratorFramework curator) {
        scheduledUploadData(curator, zkAddressNode);
    }

    private void scheduledUploadData(final CuratorFramework curator, final String zkAddressNode) {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("schedule-upload-time-%d").daemon(true).build());
        executorService.scheduleWithFixedDelay(() ->
                updateNewData(curator, zkAddressNode), 1, 3, TimeUnit.SECONDS);
    }

    private boolean checkInitTimeStamp(CuratorFramework curator, String zkAddressNode) throws Exception {
        byte[] bytes = curator.getData().forPath(zkAddressNode);
        ObjectMapper mapper = new ObjectMapper();
        Endpoint endPoint = mapper.readValue(new String(bytes), Endpoint.class);
        //该节点的时间不能小于最后一次上报的时间
        return endPoint.getTimestamp() <= System.currentTimeMillis();
    }

    private String createNode(CuratorFramework curator) throws Exception {
        try {
            return curator.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(pathForever + "/" + listenAddress + "-", buildData().getBytes());
        } catch (Exception e) {
            log.error("create node error msg {} ", e.getMessage());
            throw e;
        }
    }

    private void updateNewData(CuratorFramework curator, String path) {
        try {
            if (System.currentTimeMillis() < lastUpdateTime) {
                return;
            }
            curator.setData().forPath(path, buildData().getBytes());
            lastUpdateTime = System.currentTimeMillis();
        } catch (Exception e) {
            log.info("update init data error path is {} error is {}", path, e);
        }
    }

    private String buildData() throws JsonProcessingException {
        Endpoint endpoint = new Endpoint(ip, port, System.currentTimeMillis());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(endpoint);
    }

    private void updateLocalWorkerId(int workId) {
        File leafConfFile = new File(cachePath.replace("{port}", port));
        boolean exists = leafConfFile.exists();
        log.info("file exists status is {}", exists);
        if (exists) {
            try {
                FileUtils.writeStringToFile(
                        leafConfFile, "workId=" + workId, StandardCharsets.UTF_8, false);
                log.info("update file cache workId is {}", workId);
            } catch (IOException e) {
                log.error("update file cache error ", e);
            }
        } else {
            //不存在文件,父目录页肯定不存在
            try {
                boolean mkdirs = leafConfFile.getParentFile().mkdirs();
                log.info("init local file cache create parent dis status is {}, worker id is {}", mkdirs, workId);
                if (mkdirs) {
                    if (leafConfFile.createNewFile()) {
                        FileUtils.writeStringToFile(
                                leafConfFile, "workId=" + workId, StandardCharsets.UTF_8, false);
                        log.info("local file cache workId is {}", workId);
                    }
                } else {
                    log.warn("create parent dir error===");
                }
            } catch (IOException e) {
                log.warn("craete workId conf file error", e);
            }
        }
    }

    private CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy) {
        return CuratorFrameworkFactory.builder().connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
                .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                .build();
    }

    /**
     * 上报数据结构
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Endpoint {
        private String ip;
        private String port;
        private long timestamp;
    }

    public static class CheckLastTimeException extends RuntimeException {
        public CheckLastTimeException(String msg) {
            super(msg);
        }
    }
}
