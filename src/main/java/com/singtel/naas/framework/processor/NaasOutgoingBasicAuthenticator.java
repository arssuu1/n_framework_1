/**
 * 
 */
package com.singtel.naas.framework.processor;

import java.util.Base64;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-12-29
 */

@Component
public class NaasOutgoingBasicAuthenticator implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NaasOutgoingBasicAuthenticator.class);

    @Autowired
    protected Environment env;

    @Override
    public void process(Exchange exchange) throws Exception {

        String username = env.getProperty("provider.credential.username");
        String password = env.getProperty("provider.credential.password");

        LOGGER.info("injecting basic authenticator");
        StringBuilder sb = new StringBuilder();
        sb.append(username).append(":").append(password);
        exchange.getIn().setHeader("Authorization",
            "Basic " + Base64.getEncoder().encodeToString(sb.toString().getBytes()));

        LOGGER.debug("encoding username {}", username);
    }

}
