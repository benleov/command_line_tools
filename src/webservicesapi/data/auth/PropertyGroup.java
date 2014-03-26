package webservicesapi.data.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * A set of properties.
 */
public class PropertyGroup {

    private static final String PROPERTY_SEPERATOR = "~";
    private static final String PROPERTY_KEY_VALUE_SEPERATOR = "=";
    private Map<String, String> properties;

    public PropertyGroup() {
        super();
        properties = new HashMap<String, String>();
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public byte[] getBytes() {

        StringBuilder bytes = new StringBuilder();

        for (String key : properties.keySet()) {
            bytes.append(key);
            bytes.append(PROPERTY_KEY_VALUE_SEPERATOR);
            bytes.append(properties.get(key));
            bytes.append(PROPERTY_SEPERATOR);
        }

        if (properties.size() > 1) {
            bytes.delete(bytes.length() - (PROPERTY_SEPERATOR.length()), bytes.length());
        }

        return bytes.toString().getBytes();
    }

    public void fromBytes(byte[] bytes) {

        properties.clear();

        String toParse = new String(bytes);
        String[] pairs = toParse.split(PROPERTY_SEPERATOR);

        for (String pair : pairs) {
            String[] split = pair.split(PROPERTY_KEY_VALUE_SEPERATOR);
            properties.put(split[0], split[1]);
        }
    }

   private Map getProperties() {
        return properties;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }

    @Override
    public boolean equals(Object group) {
        if(group != null && group instanceof PropertyGroup) {
                return ((PropertyGroup) group).getProperties().equals(properties);
        }  else {
            return false;
        }
    }


}
