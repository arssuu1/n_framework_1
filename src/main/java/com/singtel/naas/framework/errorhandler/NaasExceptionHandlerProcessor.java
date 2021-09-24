package com.singtel.naas.framework.errorhandler;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.naas.framework.constant.IConstants;
import com.singtel.naas.framework.exception.NaasException;
import com.singtel.naas.framework.exception.base.NaasErrorDetail;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-01
 */
@Component
public class NaasExceptionHandlerProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaasExceptionHandlerProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        try {
            NaasException cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, NaasException.class);
            String transactionId = cause.getTransactionId();
            String correlationId = cause.getCorrelationId();

            LOGGER.debug("Handling exception for transaction order transactionId id: " + transactionId);

            ObjectMapper mapper = new ObjectMapper();

            NaasErrorDetail errorDetail = new NaasErrorDetail();
            errorDetail.setServiceName(cause.getServiceName());
            errorDetail.setConsumer("");
            errorDetail.setProviderError(cause.getProviderError() != null);
            errorDetail.setErrorCode(
                cause.getProviderError() != null ? cause.getProviderError() : String.valueOf(cause.getErrorCode()));
            errorDetail.setErrorDescription(cause.getErrorDescription());
            errorDetail.setOriginRequest(cause.getOriginalRequest());
            errorDetail.setTransactionId(transactionId);
            errorDetail.setCorrelationId(correlationId);
            errorDetail.setErrorType(String.valueOf(cause.getErrorType()));

            String jsonString = mapper.writeValueAsString(errorDetail);

            LOGGER.debug("Final JsonString set as Body: " + jsonString);

            exchange.getOut().setHeader(IConstants.EXTERNAL_TRANSACTION_ID, transactionId);
            exchange.getOut().setHeader(IConstants.INTERNAL_CORRELATION_ID, correlationId);
            exchange.getOut().setBody(jsonString);

        } catch (Exception e) {
            LOGGER.error("Exception has occurred in NaasExceptionHandlerProcessor: " + e.getMessage(), e);
        }
    }
}
