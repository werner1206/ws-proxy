package no.embriq.camel.proxy.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;

public class WireTapRoute extends RouteBuilder {

    private String wiretapBaseFolder;
    private String wiretapOn;
    private String routeId = "ProxyWiretapRoute";

    public String getWiretapBaseFolder() {
        return wiretapBaseFolder;
    }

    public void setWiretapBaseFolder(String wiretapBaseFolder) {
        this.wiretapBaseFolder = wiretapBaseFolder;
    }

    public String getWiretapOn() {
        return wiretapOn;
    }

    public void setWiretapOn(String wiretapOn) {
        this.wiretapOn = wiretapOn;
    }

    @Override
    public void configure() throws Exception {
        Predicate isWiretapOn = PredicateBuilder.constant(Boolean.valueOf(wiretapOn));

        // @formatter:off
        from("direct:wiretap").routeId(routeId)
//        .process(new Processor() {
//
//            @Override
//            public void process(Exchange exchange) throws Exception {
//             
//          
//            }
//            
//        })
            .choice()
                .when(isWiretapOn)
                    .setHeader(Exchange.FILE_NAME, 
                        simple("${header[wiretap.id]}_${header[wiretap.qualifier]}_" 
                            + "${date:now:yyyyMMdd-HHmmss.SSS}.xml"))
                    .log(LoggingLevel.INFO, "Wiretapping to: " + wiretapBaseFolder 
                        + simple("/${header[wiretap.my.folder]}/${header[" + Exchange.FILE_NAME + "]}").getText())
                    
                    .to(wiretapBaseFolder + "?fileName=" 
                        + simple("${header[wiretap.my.folder]}/${header[" + Exchange.FILE_NAME + "]}").getText())
                     
            .end();
        // @formatter:on
    }
}
