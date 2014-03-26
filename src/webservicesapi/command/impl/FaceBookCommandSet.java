package webservicesapi.command.impl;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJaxbRestClient;
import com.google.code.facebookapi.FacebookXmlRestClient;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.OutputQueue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Note: Unimplmented.
 * 
 * @author Ben Leov
 */
public class FaceBookCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(FaceBookCommandSet.class);

    private EncryptedProperties configuration;

    public FaceBookCommandSet(EncryptedProperties configuration) {
        this.configuration = configuration;
    }

    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String getCommandName()  {
                return "facebook";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

//Connection cn = DriverManager.getConnection("jdbc:saurik:fql:<apikey>;secret=<secret>;session=<session>");
//
//PreparedStatement ps = cn.prepareStatement("SELECT name, pic FROM user WHERE uid=? OR uid=?");
//ps.setLong(1, 211031);
//ps.setLong(2, 4801660);
//
//ResultSet rs = ps.executeQuery();
//
//while (rs.next()) {
//    System.out.println(rs.getString("name"));
//}


                try {

                    FacebookJaxbRestClient client = new FacebookJaxbRestClient(
                            configuration.getString("facebook.apikey"),
                            configuration.getString("facebook.secretkey"));

                    System.out.println("Is desktop:" + client.isDesktop());

                    System.out.println("Creating token");

                    String token = client.auth_createToken();

                    System.out.println("Got token: " + token);

                    GetMethod get = new GetMethod(
                            "http://www.facebook.com/login.php" +
                                    "?api_key=" + configuration.getString("facebook.apikey") +
                                    "&v=1.0&auth_token=" + token);

                    HttpClient http = new HttpClient();

                    http.setParams(new HttpClientParams());
                    http.setState(new HttpState());

                    http.executeMethod(get);

                    System.out.println("state after get: " + http.getState().toString());

                    http.setParams(new HttpClientParams());
                    http.setState(new HttpState());

                    PostMethod post = new PostMethod("http://www.facebook.com");
                    post.addParameter(new NameValuePair("api_key", configuration.getString("facebook.apikey")));
                    post.addParameter(new NameValuePair("v", "1.0"));
                    post.addParameter(new NameValuePair("fbconnect", "true"));
                    post.addParameter(new NameValuePair("return_session", "true"));
                    post.addParameter(new NameValuePair("nochrome", "true"));
                    post.addParameter(new NameValuePair("email", configuration.getString("facebook.username")));
                    post.addParameter(new NameValuePair("pass", configuration.getString("facebook.password")));

                    http.executeMethod(post);

//                    client.beginBatch();

                    logger.debug("Post method ok");

                    System.out.println("state after post: " + http.getState().toString());


                    String session = client.auth_getSession(token, true);

                    logger.debug("Got session: " + session);

                    FacebookXmlRestClient xmlClient = new FacebookXmlRestClient(
                            configuration.getString("facebook.apikey"),
                            configuration.getString("facebook.secretkey"), session, true);

                    long id = client.users_getLoggedInUser();

                    System.out.println("LOGGED IN USER ID: " + id);

//                    Collection<Long> users = new ArrayList<Long>();
//                    users.add(id);
//                    client.friends_get();
//                    UsersGetInfoResponse u = (UsersGetInfoResponse) client.getResponsePOJO();
//                    loggedUser = u.getUser().get(0);

                    //  Document d = (Document) xmlClient.fql_query("SELECT uid FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1=" + 1 + ") AND 'active' IN online_presence");


//                    FriendsGetResponse response = client.friends_get();
//
//                    System.out.println("USERIDS: ");
//                    for (Long curr : response.getUid()) {
//                        System.out.println("CURR: " + curr);
//                    }

                } catch (FacebookException e) {
                    throw new InvalidCommandException(e);
                } catch (IOException e) {
                    throw new InvalidCommandException(e);
                }

            }

            @Override
            public String getUsage() {
                return null;
            }

            @Override
            public String getHelp() {
                return "Provides facebook services";
            }

            @Override
            public String[] getRequiredProperties() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        return commands;
    }
}
