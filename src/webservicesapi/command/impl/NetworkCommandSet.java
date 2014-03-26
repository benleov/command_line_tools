package webservicesapi.command.impl;

import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Displays network information.
 *
 * @author Ben Leov
 */
public class NetworkCommandSet implements CommandSet {

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "network";
            }

            @Override
            public String[] getRequiredProperties() {
                return null;
            }

            @Override
            public Set<String> getCommandAliases() {
                Set<String> name = new HashSet<String>();
                name.add("net");
                name.add("ifconfig");
                return name;
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue)
                    throws InvalidCommandException, CommandErrorException {

                String publicIP = getPublicIPAddress();

                Output out = new Output(this);

                out.addLine("Public IP: " + publicIP);

                try {
                    Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

                    while (en.hasMoreElements()) {
                        NetworkInterface curr = en.nextElement();
                        displayInterface(curr, out);
                    }
                } catch (SocketException e) {
                    throw new CommandErrorException(e);
                }

                queue.send(out);
            }

            @Override
            public String getUsage() {
                return null;
            }

            @Override
            public String getHelp() {
                return "Provides simple networking information";
            }
        });
        return commands;
    }

    public void displayInterface(NetworkInterface inter, Output output) throws SocketException {

        if (inter.getParent() != null) {
            displayInterface(inter.getParent(), output);
        }

        output.addLine("Display Name: " + inter.getDisplayName());
        if (inter.getHardwareAddress() != null) {

            StringBuilder addr = new StringBuilder();

            for (byte curr : inter.getHardwareAddress()) {
                addr.append(curr);
                addr.append(":");
            }
            output.addLine("   Hardware Address: " + addr.toString());

        } else {
            output.addLine("No Hardware Address");
        }

        output.addLine("   MTU: " + inter.getMTU());

        Enumeration<InetAddress> en = inter.getInetAddresses();

        while (en.hasMoreElements()) {
            InetAddress address = en.nextElement();
            output.addLine("       Inet Address: " + address.getHostName());
        }
    }

    public String getPublicIPAddress() throws CommandErrorException {
        try {
            URL autoIP = new URL("http://www.whatismyip.com/automation/n09230945.asp");
            BufferedReader in = new BufferedReader(new InputStreamReader(autoIP.openStream()));
            try {
                return (in.readLine()).trim();
            } finally {
                in.close();
            }
        } catch (Exception e) {
            throw new CommandErrorException(e);
        }
    }
}


