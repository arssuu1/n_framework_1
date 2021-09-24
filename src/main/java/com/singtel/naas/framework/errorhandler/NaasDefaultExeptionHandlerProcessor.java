package com.singtel.naas.framework.errorhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.naas.data.model.Naas;
import com.singtel.naas.framework.constant.IConstants;
import com.singtel.naas.framework.exception.base.NaasErrorCode;
import com.singtel.naas.framework.exception.base.NaasErrorDetail;
import com.singtel.naas.framework.exception.base.NaasErrorType;
import com.singtel.nsb.common.util.CommonUtility;


/**
 * 
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-01
 */
@Component
public class NaasDefaultExeptionHandlerProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaasDefaultExeptionHandlerProcessor.class);
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
    	try {
            populateErrorDetail(exchange);
        } catch(Exception e) {
            // catch exception and swallow silently to avoid infinity loop: keep throwing and catch by onException(Exception)
            LOGGER.error("Unable to construct message detail to send to Kafka error topic", e);
        }
    }
    
    private void populateErrorDetail(Exchange exchange) throws JsonProcessingException {
        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        LOGGER.debug("Handling unhandled Exception Cause : ", cause.getMessage(), cause);

        NaasErrorDetail errorDetail = new NaasErrorDetail();
        errorDetail.setServiceName(exchange.getContext().getGlobalOption(IConstants.SERVICE_NAME));
        errorDetail.setConsumer(getConsumerFromRequest(exchange.getProperty(IConstants.ORIGIN_REQUEST_BODY, String.class)));
        errorDetail.setProviderError(false); 
        errorDetail.setErrorCode(String.valueOf(NaasErrorCode.UNKNOWN_EXCEPTION));
        errorDetail.setErrorDescription(extractErrorStackTrace(cause)); // beware of this one could be empty
        errorDetail.setOriginRequest(exchange.getProperty(IConstants.ORIGIN_REQUEST_BODY, String.class));
        errorDetail.setTransactionId(exchange.getIn().getHeader(IConstants.EXTERNAL_TRANSACTION_ID, String.class));
        errorDetail.setCorrelationId(exchange.getIn().getHeader(IConstants.INTERNAL_CORRELATION_ID, String.class));
        errorDetail.setErrorType(String.valueOf(NaasErrorType.TECHNICAL));

        ObjectMapper mapper = new ObjectMapper();
        exchange.getOut().setBody(mapper.writeValueAsString(errorDetail));
    }

    private String extractErrorStackTrace(Exception cause) {
        if (StringUtils.isNotBlank(cause.getMessage())) {
            return cause.getMessage();
        }
        StringWriter sw = new StringWriter();
        cause.printStackTrace(new PrintWriter(sw));
        return  sw.toString();
    }

    private String getConsumerFromRequest(String request) {
        final String rawTcpPayloadPattern = "E(.){45,}";
        if (request.matches(rawTcpPayloadPattern)) {
            return CommonUtility.getConsumerBySequenceId(request.substring(40, 46));
        } else if (extractConsumerFromNsbModel(request) != null) {
            return extractConsumerFromNsbModel(request);
        }
        return "";
    }

    private String extractConsumerFromNsbModel(String request) {
        try {
            Naas naasModel = objectMapper.readValue(request, Naas.class);
            return Optional.ofNullable(naasModel).map(model -> model.getConsumer()).orElse(null);
        } catch (IOException e) {
            LOGGER.warn("Can not parse the payload to extract the consumer. The payload is not valid JSON string", e);
            return null;
        }
    }

}
