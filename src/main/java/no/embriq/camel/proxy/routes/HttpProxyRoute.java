package no.embriq.camel.proxy.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class HttpProxyRoute extends RouteBuilder {

    private String proxyServiceURI;
    private String realServiceURI;
    private String routeId = "HttpProxyRoute";

    @Override
    public void configure() throws Exception {

        String proxyServiceEndpoint = "jetty:" 
                + proxyServiceURI + "?matchOnUriPrefix=true";        
        String realServiceEndpoint = "jetty:" 
                + realServiceURI + "?bridgeEndpoint=true&throwExceptionOnFailure=false";
        

        // @formatter:off
        from(proxyServiceEndpoint).routeId(routeId)
        .convertBodyTo(String.class, "UTF-8")
        .log(LoggingLevel.INFO, "Request message: ${body}")
        .process(new Processor() {
           
            @Override
            public void process(Exchange exchange) throws Exception {
                String uriOptions = "?bridgeEndpoint=true&throwExceptionOnFailure=false";
                String httpUri = (String) exchange.getIn().getHeader(Exchange.HTTP_URI);
                String realService = realServiceURI + httpUri + uriOptions;
                String serviceName = getServiceName(httpUri);
                exchange.getIn().setHeader("wiretap.id", serviceName);
                exchange.getIn().setHeader("wiretap.my.folder", serviceName);
                exchange.getIn().setHeader("realServiceEndpoint", realService);
            }
            
        })
        .setHeader("wiretap.qualifier", simple("Request"))
        .wireTap("direct:wiretap").end()     
        .to(realServiceEndpoint)
        //.convertBodyTo(String.class, "UTF-8")
        //.unmarshal().gzip()
        .setHeader("wiretap.qualifier", simple("Response"))
        .wireTap("direct:wiretap").end()
        .log(LoggingLevel.INFO, "Response message: ${body}"); 
        // @formatter:on

    }

    private String getServiceName(String serviceUrl) {
        return serviceUrl.substring(serviceUrl.lastIndexOf("/"));
    }

    public String getProxyServiceURI() {
        return proxyServiceURI;
    }

    public void setProxyServiceURI(String proxyServiceURI) {
        this.proxyServiceURI = proxyServiceURI;
    }

    public String getRealServiceURI() {
        return realServiceURI;
    }

    public void setRealServiceURI(String realServiceURI) {
        this.realServiceURI = realServiceURI;
    }

}
