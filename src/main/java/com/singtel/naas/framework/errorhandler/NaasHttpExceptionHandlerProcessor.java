package com.singtel.naas.framework.errorhandler;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.naas.framework.constant.IConstants;
import com.singtel.naas.framework.exception.NaasHttpException;
import com.singtel.naas.framework.exception.base.NaasErrorCode;
import com.singtel.naas.framework.exception.base.NaasErrorDetail;
import com.singtel.naas.framework.exception.base.NaasErrorType;


/**
 * 
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-01
 */
@Component
public class NaasHttpExceptionHandlerProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaasHttpExceptionHandlerProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        ObjectMapper mapper = new ObjectMapper();

        if (exception instanceof NaasHttpException) {
            NaasHttpException cause = (NaasHttpException) exception;

            String transactionId = cause.getTransactionId();
            String correlationId = cause.getCorrelationId();

            LOGGER.debug("Handling http exception for transaction order transactionId id: " + transactionId);

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
            errorDetail.setResponseMessage(cause.getResponseMessage());

            String jsonString = mapper.writeValueAsString(errorDetail);

            LOGGER.debug("Final JsonString set as Body: " + jsonString);

            exchange.setProperty(Exchange.HTTP_RESPONSE_CODE, cause.getResponseCode());
            exchange.setProperty(Exchange.HTTP_RESPONSE_TEXT, cause.getResponseMessage());

            exchange.getOut().setBody(jsonString);
        } else {
            NaasErrorDetail errorDetail = new NaasErrorDetail();
            errorDetail.setServiceName(exchange.getContext().getGlobalOption(IConstants.SERVICE_NAME));
            errorDetail.setConsumer("");
            errorDetail.setErrorCode(String.valueOf(NaasErrorCode.UNKNOWN_EXCEPTION));
            errorDetail.setCorrelationId(exchange.getIn().getHeader(IConstants.INTERNAL_CORRELATION_ID, String.class));
            errorDetail.setErrorType(String.valueOf(NaasErrorType.TECHNICAL));
            errorDetail.setErrorDescription(exception.getMessage());
            errorDetail.setOriginRequest(exchange.getIn().getBody(String.class));
            errorDetail.setResponseMessage(constructResponse(500, "Unable to process request"));

            exchange.setProperty(Exchange.HTTP_RESPONSE_CODE, 500);
            exchange.setProperty(Exchange.HTTP_RESPONSE_TEXT, constructResponse(500, "Unable to process request"));

            exchange.getOut().setBody(mapper.writeValueAsString(errorDetail));
        }
    }

    private String constructResponse(int code, String reason) throws JsonProcessingException {
        Map<String, Object> responseObject = new HashMap<>();

        responseObject.put("code", code);
        responseObject.put("reason", reason);

        return new ObjectMapper().writeValueAsString(responseObject);
    }

}
