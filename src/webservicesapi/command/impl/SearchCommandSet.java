package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.command.impl.search.APINameSpaceContext;
import webservicesapi.output.OutputQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * A search command, using the bing live search engine.
 *
 * @author Ben Leov
 */
public class SearchCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(SearchCommandSet.class);

    private static XPathFactory factory = null;
    private static XPath xpath = null;
    private static XPathExpression expr = null;

    private EncryptedProperties properties;

    public SearchCommandSet(EncryptedProperties properties) {
        this.properties = properties;
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "search";
            }

            @Override
            public String[] getRequiredProperties() {
                return new String[]{"search.appid"};
            }


            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                // Build the request.
                String requestURL = BuildRequest(parameter);

                // Send the request to the Live Search Service and get the response.
                Document doc = null;
                try {
                    doc = GetResponse(requestURL);
                } catch (Exception ex) {
                    throw new InvalidCommandException(ex);
                }

                if (doc != null) {
                    // Display the response obtained from the Live Search Service.
                    try {
                        displayResponse(doc);
                    } catch (XPathExpressionException ex) {
                        throw new InvalidCommandException(ex);
                    }
                }
            }

            @Override
            public String getUsage() {
                return "<query>";
            }

            private String BuildRequest(String query) {
                // Replace the following string with the AppId you received from the
                // Live Search Developer Center.
                String requestString = "http://api.search.live.net/xml.aspx?"

                        // Common request fields (required)
                        + "AppId=" + properties.getString("search.appid")
                        + "&Query=" + query
                        + "&Sources=Web"
                        + "&Market=en-us";

                // Common request fields (optional)
//                        + "&Version=2.0"
//                        + "&Market=en-us"
//                        + "&Adult=Moderate"
//
//                        // Web-specific request fields (optional)
//                        + "&Web.Count=10"
//                        + "&Web.Offset=0"
//                        + "&Web.FileType=DOC"
//                        + "&Web.Options=DisableHostCollapsing+DisableQueryAlterations";

                return requestString;
            }

            private Document GetResponse(String requestURL) throws ParserConfigurationException, SAXException,

                    IOException {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                Document doc = null;
                DocumentBuilder db = dbf.newDocumentBuilder();

                if (db != null) {
                    doc = db.parse(requestURL);
                }

                return doc;
            }

            private void displayResponse(Document doc) throws XPathExpressionException {
                factory = XPathFactory.newInstance();
                xpath = factory.newXPath();
                xpath.setNamespaceContext(new APINameSpaceContext());
                NodeList errors = (NodeList) xpath.evaluate("//api:Error", doc, XPathConstants.NODESET);

                if (errors != null && errors.getLength() > 0) {
                    // There are errors in the response. Display error details.
                    displayErrors(errors);
                } else {
                    displayResults(doc);
                }
            }

            private void displayResults(Document doc) throws XPathExpressionException {
                String version = (String) xpath.evaluate("//@Version", doc, XPathConstants.STRING);
                String searchTerms = (String) xpath.evaluate("//api:SearchTerms", doc, XPathConstants.STRING);
                int total = Integer.parseInt((String) xpath.evaluate("//web:Web/web:Total", doc, XPathConstants.STRING));
                int offset = Integer.parseInt((String) xpath.evaluate("//web:Web/web:Offset", doc,

                        XPathConstants.STRING));
                NodeList results = (NodeList) xpath.evaluate("//web:Web/web:Results/web:WebResult", doc, XPathConstants.NODESET);

                // Display the results header.
//                System.out.println("Live Search API Version " + version);
//                System.out.println("Web results for " + searchTerms);

                logger.info("Displaying " + (offset + 1) + " to " + (offset +
                        results.getLength()) + " of " + total + " results ");


                // Display the Web results.
//                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < results.getLength(); i++) {
                    NodeList childNodes = results.item(i).getChildNodes();

                    for (int j = 0; j < childNodes.getLength(); j++) {
                        if (!childNodes.item(j).getLocalName().equalsIgnoreCase("DisplayUrl")) {
                            String fieldName = childNodes.item(j).getLocalName();

                            if (fieldName.equalsIgnoreCase("Description")) {
                                logger.info("description: " + childNodes.item(j).getTextContent());
                            } else if (fieldName.equalsIgnoreCase("Url")) {
                                logger.info("url: " + childNodes.item(j).getTextContent());
                            }

//                            if (fieldName.equalsIgnoreCase("DateTime")) {
//                                fieldName = "Last Crawled";
//                            }
//
//                            builder.append(fieldName + ":" + childNodes.item(j).getTextContent());
//                            builder.append("\n");
                        }
                    }

//                    builder.append("\n");
                }

//                System.out.println(builder.toString());
            }

            private void displayErrors(NodeList errors) {
                System.out.println("Live Search API Errors:");
                System.out.println();

                for (int i = 0; i < errors.getLength(); i++) {
                    NodeList childNodes = errors.item(i).getChildNodes();

                    for (int j = 0; j < childNodes.getLength(); j++) {
                        System.out.println(childNodes.item(j).getLocalName() + ":" +
                                childNodes.item(j).getTextContent());
                    }

                    System.out.println();
                }
            }


            public String getHelp() {
                return "Searches the internet using the Bing search engine";
            }
        });
        return commands;
    }
}
