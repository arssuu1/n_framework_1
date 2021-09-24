/**
 * 
 */
package com.singtel.naas.framework.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.component.kafka.KafkaEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import com.singtel.naas.framework.errorhandler.NaasHttpExceptionHandlerProcessor;
import com.singtel.naas.framework.processor.NaasRestResponseProcessor;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-31
 */
@ComponentScan(basePackages = {
    "com.singtel.naas.framework"
})
public class NaasRestRouteBuilder extends NaasRouteBuilder {

    @Autowired
    protected KafkaEndpoint endpointForErrorProducer;
    
    @Autowired
    protected KafkaEndpoint endpointForAuditProducer;

    @Override
    public void configure() throws Exception {
        super.configure();

        onException(Exception.class).handled(true)
            .process(new NaasHttpExceptionHandlerProcessor())
            .multicast().parallelProcessing().to(endpointForErrorProducer, endpointForAuditProducer)
            .log(LoggingLevel.DEBUG, NaasRestRouteBuilder.class.getName(),
                "Handled Naas HttpException, Published to error topic")
            .process(new NaasRestResponseProcessor())
            .end();
    }

}
