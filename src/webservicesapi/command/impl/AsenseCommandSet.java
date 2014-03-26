package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * Unimplemented.
 *
 * @author Ben Leov
 */
public class AsenseCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(AsenseCommandSet.class);

    private EncryptedProperties properties;

    public AsenseCommandSet(EncryptedProperties properties) {
        this.properties = properties;
    }

    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            public String[] getRequiredProperties() {
                return null;
            }

            public String getCommandName()  {
                return "adsense";
            }

            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

//                package v3;
//
//                import com.google.api.adsense.v3.AccountService.AssociateAccountFault;
//                import com.google.api.adsense.v3.AccountService.V3AccountServicev3AccountServiceSOAP11Port_httpsStub;
//                import com.google.api.adsense.v3.AccountService.V3AccountServicev3AccountServiceSOAP11Port_httpsStub.SyndicationService_Data;
//
//                import org.apache.axis2.client.ServiceClient;
//                import org.apache.axiom.om.OMElement;
//
//                import java.util.Iterator;
//                import javax.xml.namespace.QName;
//
//                /**
//                 * Sample code for calling AdSense API v3's AssociateAccount method.
//                 *
//                 * @author api.wes@gmail.com (Wes Goodman)
//                 */
//                public class AssociateAccount {
//                  static final String _DEVELOPER_EMAIL = "DEVELOPER_EMAIL";
//                  static final String _DEVELOPER_PASS = "DEVELOPER_PASSWORD";
//                  static final String _ADSENSE_V3_NS = "http://www.google.com/api/adsense/v3";
//
//                  /**
//                   * @param args
//                   */
//                  public static void main(String[] args) {
//                    try {
//                      V3AccountServicev3AccountServiceSOAP11Port_httpsStub account_service =
//                          new V3AccountServicev3AccountServiceSOAP11Port_httpsStub();
//                      ServiceClient service_client = account_service._getServiceClient();
//                      service_client.addStringHeader(new QName(_ADSENSE_V3_NS,
//                                                               "developer_email"),
//                                                     _DEVELOPER_EMAIL);
//                      service_client.addStringHeader(new QName(_ADSENSE_V3_NS,
//                                                               "developer_password"),
//                                                     _DEVELOPER_PASS);
//
//                      V3AccountServicev3AccountServiceSOAP11Port_httpsStub.AssociateAccount associate_msg =
//                          new V3AccountServicev3AccountServiceSOAP11Port_httpsStub.AssociateAccount();
//                      associate_msg.setLoginEmail("USER_EMAIL");
//                      // Either-or for PostalCode and Phone, though the service will work if both are set.
//                      associate_msg.setPostalCode("USER_POSTAL_CODE");
//                      associate_msg.setPhone("USER_PHONE_NUMBER_LAST_FIVE_DIGITS");
//                      associate_msg.setDeveloperUrl("DEVELOPER_URL");
//
//                      V3AccountServicev3AccountServiceSOAP11Port_httpsStub.AssociateAccountResponse response =
//                          account_service.associateAccount(associate_msg);
//
//                      V3AccountServicev3AccountServiceSOAP11Port_httpsStub.SyndicationService_Data[] synservice_response =
//                          response.get_return();
//                      for (SyndicationService_Data syndicationService_Data : synservice_response) {
//                        System.out.println("Syndication Type: " + syndicationService_Data.getType().getValue());
//                        System.out.println("Publisher Id: " + syndicationService_Data.getId());
//                      }
//                    } catch (AssociateAccountFault fault) {
//                      System.out.println("Due to axis issues, only AxisFault will ever be thrown");
//                      System.out.println("Therefore, this catch block is for completeness only.");
//                    } catch (org.apache.axis2.AxisFault fault) {
//                      OMElement faultElement = fault.getDetail();
//                      OMElement exElement = (OMElement) (faultElement.getChildElements().next());
//                      Iterator<?> i = exElement.getChildElements();
//                      OMElement codeElement = (OMElement) i.next();
//                      OMElement internalElement = (OMElement) i.next();
//                      OMElement messageElement = (OMElement) i.next();
//                      OMElement triggerElement = (OMElement) i.next();
//                      OMElement triggerDetailsElement = (OMElement) i.next();
//                      System.out.println("Code: " + codeElement.getText());
//                      System.out.println("Internal: " + internalElement.getText());
//                      System.out.println("Message: " + messageElement.getText());
//                      System.out.println("Trigger: " + triggerElement.getText());
//                      System.out.println("Trigger Detail: " + triggerDetailsElement.getText());
//                    } catch (java.rmi.RemoteException e) {
//                      System.out.println("remote exception");
//                    }
//                  }
//                }


            }

            @Override
            public String getUsage() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public String getHelp() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        return commands;
    }
}