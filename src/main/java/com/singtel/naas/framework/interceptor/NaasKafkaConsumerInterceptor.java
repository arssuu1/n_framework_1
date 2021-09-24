/**
 * 
 */
package com.singtel.naas.framework.interceptor;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.singtel.naas.framework.constant.IConstants;


/**
 * 
 * Kafka consumer intercepter 
 * 
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-10
 */
public class NaasKafkaConsumerInterceptor<K, V> implements ConsumerInterceptor<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaasKafkaConsumerInterceptor.class);

    @Override
    public void configure(Map<String, ?> configs) {
        // Do nothing due to override method
    }

    @Override
    public ConsumerRecords<K, V> onConsume(ConsumerRecords<K, V> records) {
        
     // clearing the existing MDC
        MDC.clear();
        
        records.forEach(record -> record.headers().forEach(header -> {

            LOGGER.debug(
                "Kafka header receive : header {}, value: {}", header.key(), new String(header.value()));

            if (header.key().equals(IConstants.INTERNAL_CORRELATION_ID)) {
                MDC.put(IConstants.INTERNAL_CORRELATION_ID, new String(header.value()));
            } else if (header.key().equals(IConstants.EXTERNAL_TRANSACTION_ID)) {
                MDC.put(IConstants.EXTERNAL_TRANSACTION_ID, new String(header.value()));
            }
        }));

        return records;

    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        LOGGER.trace("commit to kafka : record offsets {}", offsets);
    }

    @Override
    public void close() {
        // Do nothing due to override method
    }
}
