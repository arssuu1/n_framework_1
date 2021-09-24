/**
 * 
 */
package com.singtel.naas.framework.route;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.ComponentScan;
import com.singtel.naas.framework.constant.IConstants;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-11-01
 */
@ComponentScan(basePackages = {
    "com.singtel.naas.framework"
})
public class NaasRouteBuilder extends RouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(NaasBaseRouteBuilder.class);

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private BuildProperties buildProperties;

    @Override
    public void configure() throws Exception {
        camelContext.getGlobalOptions().put(IConstants.SERVICE_NAME, buildProperties.getArtifact());
        camelContext.setUseMDCLogging(true);
        
        System.setProperty(IConstants.SERVICE_NAME, buildProperties.getArtifact());
        
        interceptFrom("naaskafkaconsumer.*")
            .log(LoggingLevel.DEBUG, NaasRouteBuilder.class.getName(),
                "Setting incoming payload into property[origin-request-body]")
            .setProperty(IConstants.ORIGIN_REQUEST_BODY, simple("${in.body}"));
    }

    @Override
    protected void populateRoutes() throws Exception {
        super.populateRoutes();
        // spring boot application should be able to get the build-info.properties
        // this is default for naas framework
        String artifact = "naasframework";
        if (buildProperties != null) {
            artifact = buildProperties.getArtifact();
        }

        RoutesDefinition routedefination = this.getRouteCollection();
        for (RouteDefinition route : routedefination.getRoutes()) {
            if (route.getId() != null && !route.getId().equals("")) {
                route.routeId(artifact + "-" + route.getId() + "-" + route.hashCode());
            } else {
                LOGGER.warn("Route id is not defined, please define route id for route : {} ", route);
                throw new CamelException("Route id is required for creating route");
            }
        }
    }
}
