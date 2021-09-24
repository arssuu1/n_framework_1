/**
 * 
 */
package com.singtel.naas.framework.interceptor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.apache.camel.spi.InterceptStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import com.singtel.naas.framework.constant.IConstants;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-11-02
 * This interceptor will copy the <b>external-transaction-id</b> and <b>internal-correlation-id</b> 
 * from in header to out header when processing processor under <i>com.singtel.naas</i> package
 */
@Component
public class ExchangeHeaderInterceptStrategy implements InterceptStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeHeaderInterceptStrategy.class);

    @Override
    public Processor wrapProcessorInInterceptors(CamelContext context, ProcessorDefinition<?> definition,
        Processor target, Processor nextTarget) throws Exception {
        return new DelegateAsyncProcessor(new Processor() {
            public void process(Exchange exchange) throws Exception {
                target.process(exchange);
                
                String correlationId = exchange.getIn().getHeader(IConstants.INTERNAL_CORRELATION_ID,
                    String.class) != null ? exchange.getIn().getHeader(IConstants.INTERNAL_CORRELATION_ID, String.class)
                        : MDC.get(IConstants.INTERNAL_CORRELATION_ID);

                if (exchange.hasOut()) {
                    String transactionId = exchange.getIn().getHeader(IConstants.EXTERNAL_TRANSACTION_ID,
                        String.class);

                    // setting the header if the in header is not empty
                    if (exchange.getOut().getHeader(IConstants.INTERNAL_CORRELATION_ID) == null
                        && correlationId != null) {
                        exchange.getOut().setHeader(IConstants.INTERNAL_CORRELATION_ID, correlationId);
                        LOGGER.debug("Copy in[internal-correlation-id] to out[internal-correlation-id] : {}",
                            correlationId);
                    }

                    if (exchange.getOut().getHeader(IConstants.EXTERNAL_TRANSACTION_ID) == null
                        && transactionId != null) {
                        exchange.getOut().setHeader(IConstants.EXTERNAL_TRANSACTION_ID,
                            transactionId);
                        LOGGER.debug("Copy in[external-transaction-id] to out[external-transaction-id] : {}",
                            transactionId);
                    }
                }

            }
        });
    }
}
