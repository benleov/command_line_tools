package webservicesapi.command.impl;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.command.impl.browse.HTMLParser;
import webservicesapi.command.impl.browse.HTMLParserListener;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.io.IOException;
import java.util.*;

/**
 * Not Implemented. provides quick lookups to the java api.
 */
public class JavaAPIBrowserCommandSet implements CommandSet {

    private static final Logger logger = LoggerFactory.getLogger(JavaAPIBrowserCommandSet.class);
    private ArrayList<String> packages = new ArrayList<String>();

    // string constants
    private static final String ROOT_PAGE = "http://java.sun.com/javase/6/docs/api/";
    private static final String XML_ELEMENT_IDENTIFIER = "nu.xom.Element";
    private static final String XML_TEXT_IDENTIFIER = "nu.xom.Text";
    private static final String METHOD_NAME_START_TAG = "h3";
    private static final String METHOD_NAME_END_TAG = "a";
    private static final HashSet<String> NEW_LINE_TAGS = new HashSet<String>();

    static {
        NEW_LINE_TAGS.add("ol");
        NEW_LINE_TAGS.add("ul");
        NEW_LINE_TAGS.add("li");
        NEW_LINE_TAGS.add("dl");
        NEW_LINE_TAGS.add("dt");
        NEW_LINE_TAGS.add("dd");
    }

    /* HTML tags that signify newlines should be added */

    public JavaAPIBrowserCommandSet() {
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "java-api";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException, CommandErrorException {

                final Output out = new Output(this);
                final List<String> stringsFound = new ArrayList<String>();
                final List<Node> nodeArray = new ArrayList<Node>();

                try {
                    HTMLParser parser = new HTMLParser();
                    parser.addListener(new HTMLParserListener() {
                        boolean clearVector = true;

                        @Override
                        public void onContentFound(String page, String string) {
                            if (clearVector) {
                                stringsFound.clear();
                                clearVector = false;
                            }
                            stringsFound.add(string);
                        }

                        @Override
                        public void onFinish() {
                            clearVector = true;
                        }
                    });

                    if ((parameter == null) || (parameter.lastIndexOf(".") == -1)) {
                        throw new InvalidCommandException("ERROR: Invalid class method passed in! Expected syntax is 'java-api Class.method'.");
                    }

                    // Generate list of packages from Java API; only do this once
                    if (packages.isEmpty()) {
                        parser.browse(ROOT_PAGE + "overview-frame.html", "//html:a");
                        packages.addAll(stringsFound);
                    }

                    String methodName = parameter.substring(parameter.lastIndexOf('.') + 1);
                    parameter = parameter.substring(0, parameter.lastIndexOf('.'));
                    int index = parameter.lastIndexOf('.');

                    String className = parameter.substring(parameter.lastIndexOf('.') + 1);

                    String foundItem = "";
                    for (Iterator<String> e = packages.iterator(); e.hasNext();) {
                        String nextPackage = e.next();
                        try {
                            String searchString = nextPackage + '.' + className;
                            Class.forName(searchString);
                            foundItem = searchString;
                            break;
                        } catch (ClassNotFoundException cfne) {
                        }
                    }

                    if (foundItem.isEmpty()) {
                        throw new IllegalArgumentException("ERROR! Could not find " + className + " class!");
                    }

                    String url = foundItem.replace(".", "/") + ".html";

                    // TODO: this isnt failsafe; the class may be in an external package, which
                    // wont be in the standard docs.

                    // Convert body of Java API page into Nodes
                    Nodes nodes = parser.docToHtml(ROOT_PAGE + url, "//html:body");
                    for (int x = 0; x < nodes.size(); x++) {
                        addNodeToArray(nodes.get(x), nodeArray);
                    }

                    boolean methodFound = false;
                    boolean enableOutput = false;
                    StringBuilder s = new StringBuilder();
                    for (Iterator<Node> e = nodeArray.iterator(); e.hasNext();) {
                        Node nextNode = e.next();
                        String element = nextNode.toString();

                        // The string form of each node is '[<type>: <text>]'. Some examples:
                        // [nu.xom.Element: body]
                        // [nu.xom.Text: Java Language Specification]
                        //
                        // strip off brackets and separate into two strings, type and text
                        element = element.substring(1, element.length() - 1);
                        String nodeType = element.substring(0, element.indexOf(':'));
                        String nodeValue = element.substring(element.indexOf(':') + 2);
                        String nodeString = nextNode.getValue().trim();

                        // The HTML layout for each method goes:
                        // <h3> method name </h3>
                        // ...
                        // ...
                        // <a ... name="...>
                        // Need to traverse the nodes until an <h3> is found and then compare method name with
                        // the one we are searching for. If found, output all text until an <a name=...> is encountered.
                        if (nodeType.equals(XML_ELEMENT_IDENTIFIER) && nodeValue.equals(METHOD_NAME_START_TAG)) {
                            if (nodeString.equals(methodName)) {
                                methodFound = true;
                                enableOutput = true;
                            }
                        }
                        if (enableOutput) {
                            if (nodeType.equals(XML_TEXT_IDENTIFIER)) {
                                String z = nextNode.getValue();
                                s.append(nextNode.getValue());
                            } else if (nodeType.equals(XML_ELEMENT_IDENTIFIER)) {
                                String zz = nextNode.toXML();
                                if (nodeValue.equals(METHOD_NAME_END_TAG) && (nextNode.toXML().indexOf("name=") != -1)) {
                                    enableOutput = false;
                                    out.addLine(s.toString().trim());
                                    out.addLine("====================================================");
                                    s.setLength(0);
                                } else if (NEW_LINE_TAGS.contains(nodeValue)) {
                                    if (s.charAt(s.length() - 1) != '\n') {
                                        s.append("\n");
                                    }
                                }
                            }
                        }
                    }
                    if (!methodFound) {
                        throw new IllegalArgumentException("ERROR! Could not find " + methodName + "() method!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new CommandErrorException(e);
                } catch (SAXException e) {
                    e.printStackTrace();
                    throw new CommandErrorException(e);
                } catch (ParsingException e) {
                    e.printStackTrace();
                    throw new CommandErrorException(e);
                } catch (IllegalArgumentException e) {
                    out.addLine(e.getMessage());
                }
                queue.send(out);
            }

            @Override
            public String getUsage() {
                return "<fully qualified class>";
            }

            @Override
            public String getHelp() {
                return "Extracts content from the java 6 api";
            }

            // Recursively addes the passed in node and all of its children to nodeList
            public void addNodeToArray(Node n, List<Node> nodeList) {
                nodeList.add(n);
                for (int i = 0; i < n.getChildCount(); i++) {
                    addNodeToArray(n.getChild(i), nodeList);
                }
            }
        });
        return commands;
    }
}
