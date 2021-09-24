/**
 * 
 */
package com.singtel.nsb.framework.serialization;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.apache.camel.component.kafka.serde.KafkaHeaderDeserializer;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import com.singtel.naas.framework.constant.IConstants;
import com.singtel.naas.framework.serialization.StringHeaderDeserializer;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-14
 */
public class StringHeaderDeserializerTest {
    private KafkaHeaderDeserializer deserializer = new StringHeaderDeserializer();

    @Test
    public void shouldDeserializeAsIs() {
        String value = "This is testing";

        Object deserializedValue = deserializer.deserialize(IConstants.CAMEL_BREADCRUMB_ID, value.getBytes());

        assertThat(deserializedValue, CoreMatchers.instanceOf(String.class));
        assertTrue("String should be match", deserializedValue.equals(value));
    }

    @Test
    public void shouldNotDeserializeAsIs() {
        String value = "This is testing";

        Object deserializedValue = deserializer.deserialize("somekey", value.getBytes());

        assertThat(deserializedValue, CoreMatchers.instanceOf(byte[].class));
        assertTrue("byte array should be match", java.util.Arrays.equals(value.getBytes(), (byte[]) deserializedValue));
    }
}
