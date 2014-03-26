package webservicesapi.output;

/**
 * A section of output. At present this isn't used, but in the future the layout of output may be more
 * complex.
 */
public class Section {

    private String title;

    public StringBuilder contents;

    public Section(String title) {
        this.title = title;
        contents = new StringBuilder();
    }

    public String getTitle() {
        return title;
    }

    public void addLine(String line) {
        contents.append(line);

        if (!line.endsWith("\n")) {
            contents.append('\n');
        }
    }

    public void addOutput(String output) {
        contents.append(output);
    }

    public String getOutput() {
        return contents.toString();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Section) {

            Section compare = (Section) obj;
            return compare.getTitle().equals(title) && compare.getOutput().equals(contents.toString());

        } else {
            return false;
        }

    }

}
