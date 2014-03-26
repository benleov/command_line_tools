package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.io.File;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static java.lang.management.ManagementFactory.*;

/**
 * Displays system information.
 * 
 * @author Ben Leov
 */
public class SystemInfoCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(SystemInfoCommandSet.class);

    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return null;
            }

            @Override
            public String getCommandName()  {
                return "system";
            }

            // http://support.hyperic.com/webservicesapi.init/SIGAR/Home -- TODO investigate
            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                if (parameter != null && parameter.equals("gc")) {
                    logger.info("Requesting garbage collection");
                    System.gc();
                    logger.info("Finished.");
                } else {

                    Runtime runtime = Runtime.getRuntime();

                    Output out = new Output(this);

                    out.addLine("Free RAM: " + runtime.freeMemory());
                    out.addLine("Max RAM: " + runtime.maxMemory());

                    File f = new File(".");
                    out.addLine("Free Space: " + f.getFreeSpace());
                    out.addLine("Total Space: " + f.getTotalSpace());

                    OperatingSystemMXBean opsys = getOperatingSystemMXBean();

                    out.addLine("Arch: " + opsys.getArch());
                    out.addLine("Version: " + opsys.getVersion());
                    out.addLine("Name: " + opsys.getName());
                    out.addLine("System Average Load: " + opsys.getSystemLoadAverage());
                    out.addLine("Available Processers: " + opsys.getAvailableProcessors());

                    MemoryMXBean mem = getMemoryMXBean();

                    out.addLine("Heap Max: " + mem.getHeapMemoryUsage().getMax());
                    out.addLine("Heap Used: " + mem.getHeapMemoryUsage().getUsed());

                    out.addLine("Non-heap Max: " + mem.getNonHeapMemoryUsage().getMax());
                    out.addLine("Non-heap Used: " + mem.getNonHeapMemoryUsage().getUsed());

                    RuntimeMXBean rt = getRuntimeMXBean();

                    out.addLine("Boot class path: " + rt.getBootClassPath());
                    out.addLine("Class Path: " + rt.getClassPath());
                    out.addLine("Uptime: " + rt.getUptime());
                    out.addLine("VM Vendor: " + rt.getVmVendor());
                    out.addLine("Start Time: " + rt.getStartTime());

                    ThreadMXBean threads = getThreadMXBean();

                    out.addLine("Peak Thread Count: " + threads.getPeakThreadCount());
                    out.addLine("Thread Count: " + threads.getThreadCount());
                    out.addLine("Daemon Thread Count: " + threads.getDaemonThreadCount());
                    out.addLine("");

                    Properties props = System.getProperties();

                    for (Object key : props.keySet()) {
                        out.addLine(key + " : " + props.getProperty((String) key));
                    }

                    queue.send(out);

                }

            }

            @Override
            public String getUsage() {
                return "[gc]";
            }

            @Override
            public String getHelp() {
                return "Displays operation system information";
            }
        });
        return commands;
    }
}
