package co.id.yokke.multiacquiring.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class MessagePropertiesCache
{
    private final Properties configProp = new Properties();

    private MessagePropertiesCache()
    {
        String propertiesName = "message.properties";
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(propertiesName);
        try {
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class LazyHolder
    {
        private static final MessagePropertiesCache INSTANCE = new MessagePropertiesCache();
    }

    public static MessagePropertiesCache getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key){
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames(){
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key){
        return configProp.containsKey(key);
    }
}
