package no.embriq.camel.proxy;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class SampleService implements Processor {
    public void process(Exchange exchange) throws Exception {
        // just get the body as a string
        // String body = exchange.getIn().getBody(String.class);

        // Get the bookId Header
        String bookId = (String) exchange.getIn().getHeader("bookid");

        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
        exchange.getOut().setBody("<html><body>Book " + bookId + " is Camel in Action.</body></html>");
    }
}
