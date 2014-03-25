package no.embriq.camel.proxy;

import no.embriq.camel.proxy.routes.HttpProxyRoute;
import no.embriq.camel.proxy.routes.WireTapRoute;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A main program to start a http proxy and wiretap requests and responses
 * between client and server
 * 
 * <p>
 * Usage:
 * 
 * java -Dwsproxy.properties=/path/to/wsproxy.properties
 * com.colorline.clib.proxy.HttpProxy
 * 
 * @author truls.flomark
 */
public final class HttpProxy {

    // private Main main;
    private CamelContext camelContext;
    private ApplicationContext appContext;
    private static Logger log = LoggerFactory.getLogger(HttpProxy.class);
    
    private HttpProxy() {
        
    }

    public static void main(String[] args) throws Exception {

//        if (System.getProperty("wsproxy.properties") == null) {
//            System.out
//                    .println("Usage: java -jar -Dwsproxy.properties=/path/to/wsproxy.properties  ws-proxy.jar");
//        } else {
//            HttpProxy proxy = new HttpProxy();
//            proxy.start();
//        }

        HttpProxy proxy = new HttpProxy();
        proxy.start();
    }

    private void start() throws Exception {

        appContext = new ClassPathXmlApplicationContext("appContext.xml");

        camelContext = new DefaultCamelContext();

        HttpProxyRoute proxyRoute = (HttpProxyRoute) appContext.getBean("proxyRoute");
        WireTapRoute wiretapRoute = (WireTapRoute) appContext.getBean("wiretapRoute");

        camelContext.addRoutes(wiretapRoute);
        camelContext.addRoutes(proxyRoute);
        camelContext.setUseMDCLogging(true);
        camelContext.setTracing(false);

        // Start camel
        camelContext.start();
        log.info("HttpProxy started for {}", camelContext);
    }

}
