package webservicesapi.output;

import webservicesapi.command.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the output from a command.
 *
 * @author Ben Leov
 */
public class Output {

    private Class<? extends Command> owner;
    private List<Section> sections;
    private Section current;
    private String name;

    private Output(Class<? extends Command> owner) {
        this.owner = owner;
        sections = new ArrayList<Section>();
    }

    public Output(Command command) {
        this(command.getClass());
        this.name = command.getCommandName();
    }


    public Output(Command owner, String output) {
        this(owner);
        Section section = new Section(name);
        section.addLine(output);
        sections.add(section);
        current = section;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Command> getOwner() {
        return owner;
    }

    public Section addSection(String title) {
        Section s = new Section(title);
        sections.add(s);
        current = s;
        return s;
    }

    public Section addLine(String line) {
        if (current == null) {
            current = new Section(name);
            sections.add(current);
        }

        current.addLine(line);
        return current;
    }

    public Section addLine() {
        return addLine("");
    }

    public Section getCurrentSection() {
        return current;
    }

    /**
     * Returns all the sections contained within this output. Note that the list returned via this method
     * cannot be modified.
     * 
     * @return
     */
    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Output) {

            Output toCompare = (Output) obj;

            if (toCompare.getName().equals(name) && toCompare.getOwner().equals(owner)
                    && sections.size() == toCompare.getSections().size()) {
                // name and owner match; test each section
                int x = 0;
                for (Section curr : toCompare.getSections()) {

                    if (!curr.equals(sections.get(x))) {
                        return false; // section not equals
                    }
                    x++;
                }
                return true;
            } else {
                return false;  // name, owner or number of sections don't match
            }
        } else {
            return false;  // wrong object type
        }
    }

}
