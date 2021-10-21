package com.orange.demo.operationlogconsumer.consumer;

import com.alibaba.fastjson.JSON;
import com.orange.demo.common.log.model.SysOperationLog;
import com.orange.demo.common.log.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 各个微服务操作日志的消费者对象。该消费者会集中处理操作日志。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Component
@Slf4j
public class OperationLogConsumer {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Bean
    public KafkaListenerContainerFactory<?> batchFactory(ConsumerFactory<Integer, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(10);
        factory.getContainerProperties().setPollTimeout(1500);
        //设置为批量消费，每个批次数量在Kafka配置参数中设置
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @KafkaListener(
            topics = {"${common-log.operation-log.kafkaTopic}"},
            containerFactory = "batchFactory",
            groupId = "operation-log")
    public void listen(List<ConsumerRecord<?, ?>> recordList, Acknowledgment ack) {
        if (CollectionUtils.isNotEmpty(recordList)) {
            List<SysOperationLog> operationLogList = new LinkedList<>();
            for (ConsumerRecord<?, ?> record : recordList) {
                Optional<?> message = Optional.ofNullable(record.value());
                if (message.isPresent()) {
                    SysOperationLog operationLog =
                            JSON.parseObject(message.get().toString(), SysOperationLog.class);
                    operationLogList.add(operationLog);
                }
            }
            if (CollectionUtils.isNotEmpty(operationLogList)) {
                try {
                    sysOperationLogService.batchSave(operationLogList);
                } catch (Exception e) {
                    log.error("Failed to batchSave SysOperationLog and try again one by one", e);
                    this.safeSave(operationLogList);
                }
            }
        }
        ack.acknowledge();
    }

    private void safeSave(List<SysOperationLog> operationLogList) {
        for (SysOperationLog operationLog : operationLogList) {
            try {
                // 如果批量插入失败，为了确保最大限度的将操作日志数据插入到目的表。
                // 这里我们将逐条插入，对于重复插入直接忽略错误，对于其他异常，需要用户修改后自行处理。
                sysOperationLogService.saveNew(operationLog);
            } catch (DuplicateKeyException e1) {
                // 出现重复的场景，很大可能是因为之前插入数据库成功了，但是提交kafka offset失败了。
                // 因此就会出现重复消费的情况，我们通过logId主键进行了去重。
                log.warn("Duplicated Key for logId [{}]", operationLog.getLogId());
            }
        }
    }
}
