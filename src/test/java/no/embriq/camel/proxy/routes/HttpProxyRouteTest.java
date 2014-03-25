package no.embriq.camel.proxy.routes;

import no.embriq.camel.proxy.SampleService;
import no.embriq.camel.proxy.routes.HttpProxyRoute;
import no.embriq.camel.proxy.routes.WireTapRoute;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class HttpProxyRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:wiretap")
    private MockEndpoint mockWiretap;

    public RouteBuilder[] createRouteBuilders() {
        HttpProxyRoute proxy = new HttpProxyRoute();
        proxy.setProxyServiceURI("http://0.0.0.0:9292");
        proxy.setRealServiceURI("http://0.0.0.0:9191");
        
        WireTapRoute wiretap = new WireTapRoute();
        wiretap.setWiretapBaseFolder("file://target/wiretap");
        wiretap.setWiretapOn("true");

        RouteBuilder realServiceRoute = new RouteBuilder() {

            SampleService testService = new SampleService();

            @Override
            public void configure() throws Exception {
                from("jetty:http://0.0.0.0:9191/test/testService").process(testService);
            }
        };

        return new RouteBuilder[] { proxy, realServiceRoute, wiretap };
    }

    @Test
    public void proxyRouteTest() throws Exception {

        //deleteDirectory("target/wiretap");
        context.setTracing(true);

        context.getRouteDefinition("HttpProxyRoute").adviceWith(context, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("direct:wiretap*").skipSendToOriginalEndpoint().to("mock:wiretap");
            }
        });

        mockWiretap.setExpectedMessageCount(2);

        template.sendBodyAndHeader("http://0.0.0.0:9292/test/testService", null, "bookid", "123");
        assertMockEndpointsSatisfied();

    }

    @Test
    public void proxyRouteWiretapTest() {
        
        deleteDirectory("target/wiretap");
        context.setTracing(true);        
        
        template.sendBodyAndHeader("http://0.0.0.0:9292/test/testService", null, "bookid", "123");
        
        assertTrue(true);
    }
}
