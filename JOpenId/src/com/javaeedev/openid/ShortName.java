package com.javaeedev.openid;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Store short names which are mapping to providers' urls.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class ShortName {

    private Map<String, String> urlMap = new HashMap<String, String>();

    public ShortName() {
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream("openid-providers.properties");
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

    String lookupUrlByName(String name) {
        return urlMap.get(name);
    }
}
