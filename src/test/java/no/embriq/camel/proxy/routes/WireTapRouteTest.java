package no.embriq.camel.proxy.routes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import no.embriq.camel.proxy.routes.WireTapRoute;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class WireTapRouteTest extends CamelTestSupport {

    public RouteBuilder[] createRouteBuilders() {
        WireTapRoute wtRouteEnabled = new WireTapRoute();

        wtRouteEnabled.setWiretapBaseFolder("file://target/wiretap");
        wtRouteEnabled.setWiretapOn("true");

        return new RouteBuilder[] { wtRouteEnabled };
    }

    @Test
    public void testWireTapEnabled() throws Exception {
        deleteDirectory("target/wiretap");

        String payload = "Test message";

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("wiretap.qualifier", "Request");
        headers.put("wiretap.id", "MyServiceName");
        headers.put("wiretap.my.folder", "folder");
        template.sendBodyAndHeaders("direct:wiretap", payload, headers);

        Thread.sleep(1000);

        File folder = new File("target/wiretap/folder");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                assertTrue(listOfFiles[i].getName().contains("MyServiceName"));
                return;
            }
        }

        assertFalse(true);
    }

}
