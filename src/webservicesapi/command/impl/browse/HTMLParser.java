package webservicesapi.command.impl.browse;

import nu.xom.*;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ben Leov
 */
public class HTMLParser {

    private Logger logger = LoggerFactory.getLogger(HTMLParser.class);

    private Set<HTMLParserListener> listeners;

    public HTMLParser() {
        listeners = new HashSet<HTMLParserListener>();
    }

    // Convert an XML Document to a collection of Nodes based on an xpath expression.
    public Nodes docToHtml(String address, String xpath) throws IOException, SAXException, ParsingException {
        HttpClient http = new HttpClient();
        final GetMethod get = new GetMethod(address);
        get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

        http.executeMethod(get);

        XMLReader tagsoup = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser");
        tagsoup.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        Builder bob = new Builder(tagsoup);

        Document doc = bob.build(get.getResponseBodyAsStream());

//        System.out.println("DOC: " + doc.toXML());

        XPathContext context = new XPathContext("html", "http://www.w3.org/1999/xhtml");

        return doc.query(xpath, context);
    }

    public void browse(String address, String xpath) throws IOException, SAXException, ParsingException {
        Nodes table = docToHtml(address, xpath);
        logger.debug("Displaying page " + address);
        for (int x = 0; x < table.size(); x++) {
            Node curr = table.get(x);

            notifyListeners(address, curr);
        }

        onFinish();
    }

    private void notifyListeners(String address, Node current) {

        if (current.getChildCount() > 0) {
            for (int x = 0; x < current.getChildCount(); x++) {
                Node child = current.getChild(x);
                notifyListeners(address, child);
            }
        } else if (!"".equals(current.getValue().trim())) {
            notifyListeners(address, current.getValue());
        }
    }

    private void notifyListeners(String page, String content) {
        for (HTMLParserListener listener : listeners) {
            listener.onContentFound(page, content);
        }
    }

    private void onFinish() {
        for (HTMLParserListener listener : listeners) {
            listener.onFinish();
        }
    }

    public void addListener(HTMLParserListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HTMLParserListener listener) {
        listeners.remove(listener);
    }
}
