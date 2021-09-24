/**
 * 
 */
package com.singtel.naas.framework.errorhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.naas.framework.constant.IConstants;
import com.singtel.naas.framework.exception.base.NaasErrorCode;
import com.singtel.naas.framework.exception.base.NaasErrorDetail;
import com.singtel.naas.framework.exception.base.NaasErrorType;
import com.singtel.naas.data.model.Naas;
import com.singtel.nsb.mobile.util.constant.CommonConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * The purpose of this exception handler is to handle 
 * <ul>
 * 		<li>The retriable exceptions thrown by micro service</li>
 * 		<li>This also handles the exhaustion of number of retries and submit to Error / audit topic </li>
 * 		<li>Nsb internalStatus is set to FAILED_FOR_RETRY or RETRY_EXHAUSTED as appropriate</li>
 * </ul>
 *  
 */
@Component
public class NaasRetriableExceptionHandler implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaasRetriableExceptionHandler.class);

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void process(Exchange exchange) throws Exception {
        String originBody = exchange.getProperty(IConstants.ORIGIN_REQUEST_BODY, String.class);
        Naas naasObject = mapper.readValue(originBody, Naas.class);
        
        // temporary put the request here to send to ErrorTopic later
        String errorDetailString = createNsbErrorDetailAsString(exchange);
        exchange.setProperty(IConstants.TEMP_REQUEST_BODY, errorDetailString);

        // check if the request already been retried
        boolean retried = CommonConstants.INTERNAL_STATUS_FAILED_FOR_RETRY
            .equalsIgnoreCase(naasObject.getInternalStatus());

        if (!retried) {
            // this information will be send back to consumer topic to the next retry round
            exchange.setProperty(CommonConstants.INTERNAL_STATUS_FAILED_FOR_RETRY, true);
            naasObject.setInternalStatus(CommonConstants.INTERNAL_STATUS_FAILED_FOR_RETRY);
            String body = mapper.writeValueAsString(naasObject);
            exchange.getIn().setBody(body);
            LOGGER.info("Getting origin body to publish to for reprocess: {}", body);
        } else {
            // this information will be send to audit topic
            naasObject.setInternalStatus(CommonConstants.INTERNAL_STATUS_FAILED_RETRY_EXHAUSTED);
            String auditInfo = mapper.writeValueAsString(naasObject);
            exchange.getIn().setHeader(IConstants.EXTERNAL_TRANSACTION_ID, naasObject.getExternalTransactionId());
            exchange.getIn().setHeader(IConstants.INTERNAL_CORRELATION_ID, naasObject.getInternalCorrelationId());
            exchange.getIn().setBody(auditInfo);
            LOGGER.info("Done constructing Naas Audit Info: {}", auditInfo);
        }
    }

    private String createNsbErrorDetailAsString(Exchange exchange) throws IOException {
        String originBody = exchange.getProperty(IConstants.ORIGIN_REQUEST_BODY, String.class);
        Naas naasObject = mapper.readValue(originBody, Naas.class);
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        NaasErrorDetail errorDetail = new NaasErrorDetail();
        errorDetail.setServiceName(exchange.getContext().getGlobalOption(IConstants.SERVICE_NAME));
        errorDetail.setConsumer(naasObject.getConsumer());
        errorDetail.setErrorDescription(exception.getMessage());
        errorDetail.setErrorCode(String.valueOf(NaasErrorCode.PROVIDER_UNAVAILABLE));
        errorDetail.setOriginRequest(originBody);
        errorDetail.setTransactionId(naasObject.getExternalTransactionId());
        errorDetail.setCorrelationId(naasObject.getInternalCorrelationId());
        errorDetail.setErrorType(String.valueOf(NaasErrorType.TECHNICAL));
        return mapper.writeValueAsString(errorDetail);
    }
}
