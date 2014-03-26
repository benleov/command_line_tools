package webservicesapi.command.impl.favorite;

/**
 * @author Ben Leov
 */
public class Favorite {

    private String alias;
    private String commandName;
    private String parameters;

    public Favorite(String alias, String commandName, String parameters) {
        this.setAlias(alias);
        this.setCommandName(commandName);
        this.setParameters(parameters);
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Favorite favorite = (Favorite) o;

        if (!alias.equals(favorite.alias)) return false;
        if (!commandName.equals(favorite.commandName)) return false;
        if (parameters != null ? !parameters.equals(favorite.parameters) : favorite.parameters != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = alias.hashCode();
        result = 31 * result + commandName.hashCode();
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }
}
