/**
 * 
 */
package com.singtel.naas.framework.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.naas.framework.constant.IConstants;
import com.singtel.naas.framework.exception.base.NaasErrorDetail;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-11-01
 */
public class NaasRestResponseProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NaasRestResponseProcessor.class);

    private static final String MEDIA_TYPE_APPLICATION_JSON = "application/json";

    @Override
    public void process(Exchange exchange) throws Exception {

        String body = exchange.getIn().getBody(String.class);
        LOGGER.debug("Composing resposne, body from Exchange : \r\n {}", body);

        ObjectMapper objMapper = new ObjectMapper();
        NaasErrorDetail errorDetail = objMapper.readValue(body, NaasErrorDetail.class);

        exchange.getIn().getHeaders().clear();

        exchange.getOut().setHeader(IConstants.INTERNAL_CORRELATION_ID, errorDetail.getCorrelationId());

        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
        exchange.getProperty(Exchange.HTTP_RESPONSE_CODE));
        exchange.getOut().setHeader(Exchange.CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON);
        exchange.getOut().setBody(errorDetail.getResponseMessage());
    }

}
