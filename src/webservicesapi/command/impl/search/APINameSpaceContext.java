package webservicesapi.command.impl.search;

/**
 * @author Ben Leov
 */

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

// Map prefixes to Namespace URIs
public class APINameSpaceContext implements NamespaceContext {
    private static final String WEB_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web";
    private static final String API_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/element";
    private static final String SPELL_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/spell";
    private static final String RS_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/relatedsearch";
    private static final String PB_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/phonebook";
    private static final String MM_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/multimedia";
    private static final String AD_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/ads";
    private static final String IA_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/instantanswer";
    private static final String NEWS_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news";
    private static final String ENCARTA_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/encarta";

    public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("api".equals(prefix)) return API_NAMESPACE;
        else if ("web".equals(prefix)) return WEB_NAMESPACE;
        return XMLConstants.NULL_NS_URI;
    }

    // This method isn't necessary for XPath processing.
    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    public Iterator getPrefixes(String arg0) {
        throw new UnsupportedOperationException();
    }
}