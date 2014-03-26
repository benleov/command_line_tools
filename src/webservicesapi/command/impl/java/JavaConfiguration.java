package webservicesapi.command.impl.java;

import java.io.File;
import java.util.List;

/**
 * @author Ben Leov
 */
public class JavaConfiguration {

    private File jar, source;
    private List<File> libraries;
    private String mainClass, name;
    private List<String> options;

    public JavaConfiguration(String name) {
        this.name = name;

        options = null;
    }

    /**
     * A new java application that is running from a jar file
     *
     * @param name
     * @param jar
     * @param libraries
     * @param mainClass
     */
    public JavaConfiguration(String name, File jar,
                             List<File> libraries, String mainClass) {
        this(name);
        this.jar = jar;
        this.libraries = libraries;
        this.mainClass = mainClass;

        source = null;
    }

    /**
     * Constructs a new java application that will be run from source.
     *
     * @param name
     * @param source
     * @param libraries
     * @param mainClass
     */
    public JavaConfiguration(File source, String name,
                             List<File> libraries, String mainClass) {

        this(name, null, libraries, mainClass);
        this.source = source;

        this.jar = null;
    }


    public boolean isValid() {
        return (jar != null && !jar.isDirectory() ||
                source != null && source.isDirectory()) &&
                mainClass != null && mainClass.trim().length() != 0;
    }

    public String getName() {
        return name;
    }

    public void setJar(File jar) {
        this.jar = jar;
    }

    public void setLibraries(List<File> libraries) {
        this.libraries = libraries;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public File getJar() {
        return jar;
    }

    public List<File> getLibraries() {
        return libraries;
    }

    public String getMainClass() {
        return mainClass;
    }

    public File getSource() {
        return this.source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<String> getOptions() {
        return this.options;
    }
}
