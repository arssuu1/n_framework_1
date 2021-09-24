package com.singtel.naas.framework.route;

import java.net.ConnectException;
import java.net.SocketException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.component.kafka.KafkaEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import com.singtel.naas.common.util.logger.LoggerFactory;
import com.singtel.naas.framework.errorhandler.NaasDefaultExeptionHandlerProcessor;
import com.singtel.naas.framework.errorhandler.NaasExceptionHandlerProcessor;
import com.singtel.naas.framework.errorhandler.NaasRetriableExceptionHandler;
import com.singtel.naas.framework.exception.NaasException;


/**
 * 
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-01
 */
@ComponentScan(basePackages = {
    "com.singtel.naas.framework"
})
public class NaasBaseRouteBuilder extends NaasRouteBuilder {

    @Value("${default.retryable.delaypattern:3:5000}")
    private String redeliveryDelayPattern;

    @Value("${default.retryable.count:3}")
    private int retryCount;
    
    @Autowired
    protected Environment env;

    @Autowired
    private NaasExceptionHandlerProcessor naasExceptionHandlerProcessor;

    @Autowired
    protected NaasDefaultExeptionHandlerProcessor naasDefaultExeptionHnadlerProcessor;

    @Autowired
    protected NaasRetriableExceptionHandler naasRetriableExceptionHandler;
    
    @Autowired
    protected KafkaEndpoint endpointForErrorProducer;
    
    @Autowired
    protected KafkaEndpoint endpointForRetryErrorProducer;

    @Autowired
    protected KafkaEndpoint endpointForRetryProducer;
    
    @Autowired
    public KafkaEndpoint endpointForRetryAuditProducer;

    @SuppressWarnings("unchecked")
	@Override
    public void configure() throws Exception {
        super.configure();

        errorHandler(defaultErrorHandler().maximumRedeliveries(0).redeliveryDelay(0));

        onException(NaasException.class).handled(true)
            .process(naasExceptionHandlerProcessor)
            .multicast().parallelProcessing().to(endpointForRetryErrorProducer, endpointForRetryAuditProducer)
            .log(LoggingLevel.DEBUG, NaasBaseRouteBuilder.class.getName(),
                "Handled NaasException, Published to error and audit topic")
            .end();

        onException(Exception.class).handled(true)
            .process(naasDefaultExeptionHnadlerProcessor)
            .multicast().parallelProcessing().to(endpointForRetryErrorProducer, endpointForRetryAuditProducer)
            .log(LoggingLevel.DEBUG, NaasBaseRouteBuilder.class.getName(),
                "Handled Generic Exception, Published to error topic and audit topic")
            .end();

     // if the retry exhausted, place the message back to consumer topic for retry later
        onException(ConnectException.class, SocketException.class).handled(true)
            .log(LoggingLevel.WARN, LoggerFactory.BUSINESS_LOG,
                "Handling exception cause ${exchangeProperty.CamelExceptionCaught}")
            .maximumRedeliveries(retryCount)
            .delayPattern(redeliveryDelayPattern)
            .logRetryAttempted(true)
            .retryAttemptedLogLevel(LoggingLevel.WARN)
            .end()
            .process(naasRetriableExceptionHandler)
            .choice()
                .when(simple("${exchangeProperty.FAILED_FOR_RETRY} == true"))
                .log(LoggingLevel.INFO, LoggerFactory.BUSINESS_LOG, "placing request back to topic for retry")
                .to(endpointForRetryProducer)
                .setBody(simple("${exchangeProperty[temp-request-body]}"))
                .to(endpointForRetryErrorProducer)
            .otherwise()
                .log(LoggingLevel.ERROR, LoggerFactory.BUSINESS_LOG,
                "Failed to deliver the request due to ${exchangeProperty.CamelExceptionCaught}")
                .log(LoggingLevel.INFO, LoggerFactory.BUSINESS_LOG, "Retry exceeded publish to audit topic")
                .to(endpointForRetryAuditProducer)
                .setBody(simple("${exchangeProperty[temp-request-body]}"))
                .to(endpointForRetryErrorProducer)
                .log(LoggingLevel.INFO, LoggerFactory.BUSINESS_LOG, "publish to error topic for failure")
            .end();
    }

}
