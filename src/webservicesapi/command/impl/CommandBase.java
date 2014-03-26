package webservicesapi.command.impl;

import webservicesapi.command.Command;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ben Leov
 */
public abstract class CommandBase implements Command {

    /**
     * By default the command has no alias.
     *
     * @return
     */
    @Override
    public Set<String> getCommandAliases() {
        return new HashSet<String>();
    }

    /**
     * By default, no optional properties.
     *
     * @return
     */
    @Override
    public String[] getOptionalProperties() {
        return null;
    }

    /**
     * By default no required properties.
     * 
     * @return
     */
    @Override
    public String[] getRequiredProperties() {
        return null;
    }
}
