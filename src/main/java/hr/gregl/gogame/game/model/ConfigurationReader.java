package hr.gregl.gogame.game.model;

import hr.gregl.gogame.game.exception.InvalidConfigurationKeyException;
import hr.gregl.gogame.game.jndi.InitialDirContextCloseable;
import hr.gregl.gogame.game.utility.LogUtil;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class ConfigurationReader {

    private static ConfigurationReader reader;

    private final Hashtable<String, String> environment;

    private ConfigurationReader() {
        environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL, "file:./ThisIsVerySecretFileConfig");
    }

    public static ConfigurationReader getInstance() {
        if (reader == null) {
            reader = new ConfigurationReader();
        }
        return reader;
    }

    public Integer readIntegerValueForKey(ConfigurationKey key) {
        String valueForKey = readStringValueForKey(key);
        return Integer.parseInt(valueForKey);
    }

    public String readStringValueForKey(ConfigurationKey key) {
        String valueForKey = "";

        try (InitialDirContextCloseable context = new InitialDirContextCloseable(environment)) {
            valueForKey = searchForKey(context, key);
        } catch (NamingException e) {
            LogUtil.logError(e);
        }

        return valueForKey;
    }

    private String searchForKey(Context context, ConfigurationKey key) {
        String fileName = "config.properties";

        try {
            Object object = context.lookup(fileName);
            Properties props = new Properties();
            props.load(new FileReader(object.toString()));
            String value = props.getProperty(key.getKeyName());
            if (value == null) {
                throw new InvalidConfigurationKeyException("The key '" + key.getKeyName() + "' does not exist in configuration file!");
            }
            return value;
        } catch (NamingException | IOException ex) {
            throw new RuntimeException("Error while reading configuration!", ex);
        }
    }
}
