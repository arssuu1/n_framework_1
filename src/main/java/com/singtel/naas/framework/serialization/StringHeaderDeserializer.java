/**
 * 
 */
package com.singtel.naas.framework.serialization;

import org.apache.camel.component.kafka.serde.KafkaHeaderDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.singtel.naas.framework.constant.IConstants;


/**
 * 
 * Kafka header string deserializer from byte array
 * 
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-10
 */

public class StringHeaderDeserializer implements KafkaHeaderDeserializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringHeaderDeserializer.class);

    @Override
    public Object deserialize(String key, byte[] value) {

        if (key.equalsIgnoreCase(IConstants.EXTERNAL_TRANSACTION_ID)
            || key.equalsIgnoreCase(IConstants.UBER_TRACE_ID)
            || key.equalsIgnoreCase(IConstants.INTERNAL_CORRELATION_ID) ||
            key.contentEquals(IConstants.CAMEL_BREADCRUMB_ID)) {

            LOGGER.debug("deserialize key : {} , value : {}", key, new String(value));

            return new String(value);
        }
        return value;
    }
}
