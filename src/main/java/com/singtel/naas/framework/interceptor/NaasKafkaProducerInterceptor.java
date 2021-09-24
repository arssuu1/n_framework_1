/**
 * 
 */
package com.singtel.naas.framework.interceptor;

import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.singtel.naas.framework.constant.IConstants;


/**  
 * 
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-10
 * 
 */
public class NaasKafkaProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaasKafkaProducerInterceptor.class);

    @Override
    public void configure(Map<String, ?> configs) {
        // Do nothing due to override method
    }

    @Override
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
        boolean isTransactionIdExist = false;
        for (Header header : record.headers()) {
            if (header.key().equalsIgnoreCase(IConstants.EXTERNAL_TRANSACTION_ID)) {
                isTransactionIdExist = true;
            }
        }

        if (!isTransactionIdExist && MDC.get(IConstants.EXTERNAL_TRANSACTION_ID) != null) {
            LOGGER.debug("transaction id is not found in the header, adding transaction id to header {}",
                MDC.get(IConstants.EXTERNAL_TRANSACTION_ID));

            record.headers().add(IConstants.EXTERNAL_TRANSACTION_ID,
                (MDC.get(IConstants.EXTERNAL_TRANSACTION_ID)).getBytes());
        }
        
        if (System.getProperty(IConstants.SERVICE_NAME) != null) {
            record.headers().add(IConstants.SERVICE_NAME, System.getProperty(IConstants.SERVICE_NAME).getBytes());
        }
        
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            LOGGER.error("Unable to send message to Kafka", exception);
            return;
        }
        LOGGER.info("Send message to Kafka successfully. Ack metadata: " + metadata);
    }

    @Override
    public void close() {
        // Do nothing due to override method
    }

}
