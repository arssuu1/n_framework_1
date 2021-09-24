/**
 * 
 */
package com.singtel.nsb.framework.route;

import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelException;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;
import com.singtel.naas.framework.route.NaasBaseRouteBuilder;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-14
 */
public class NsbBaseRouteBuilderTest extends TestSupport {

    protected List<Route> buildNsbSimpleRoute() throws Exception {
        RouteBuilder builder = new NaasBaseRouteBuilder() {
            public void configure() {
                from("direct:a").to("direct:b");
            }
        };
        return getRouteList(builder);
    }

    @Test(expected = CamelException.class)
    public void testSimpleRoute() throws Exception {
        buildNsbSimpleRoute();
    }
}
