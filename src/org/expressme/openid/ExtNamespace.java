package org.expressme.openid;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Store short names which are mapping to extension namespace aliases.
 * This class provides a way to determine what extension namespace
 * alias to use for a given openid provider.
 * 
 * @author Erwin Quinto (erwin.quinto@gmail.com)
 */
public class ExtNamespace {
	public static final String DEFAULT_ALIAS = "ext1";

    private Map<String, String> urlMap = new HashMap<String, String>();

    /**
     * Load short names from "extension-namespace.properties" under class path.
     */
    public ExtNamespace() {
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream("extension-namespace.properties");
            Properties props = new Properties();
            props.load(input);
            for (Object k : props.keySet()) {
                String key = (String) k;
                String value = props.getProperty(key);
                urlMap.put(key, value);
            }
        }
        catch (IOException e) {
            // load failed:
            e.printStackTrace();
        }
        finally {
            if (input!=null) {
                try {
                    input.close();
                }
                catch (IOException e) {}
            }
        }
    }

    String getAlias(String name) {
    	String alias = urlMap.get(name);
    	if (alias == null) {
    		alias = DEFAULT_ALIAS;
    	}
        return alias;
    }
}
