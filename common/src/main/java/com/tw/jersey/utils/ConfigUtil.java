package com.tw.jersey.utils;

import com.google.common.base.Strings;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

import java.io.File;

public class ConfigUtil {
    private static final String CONFIG_FILE_PATH_PROPERTY = "config_file";

    private static final Logger LOG = Logger.getLogger(ConfigUtil.class);
    private static Configuration configuration = loadConfiguration();

    private ConfigUtil() {
    }

    private static Configuration loadConfiguration() {
        try {
            CompositeConfiguration compositeConfiguration = new CompositeConfiguration();

            String configFilePath = System.getProperty(CONFIG_FILE_PATH_PROPERTY);
            if (!Strings.isNullOrEmpty(configFilePath)) {
                LOG.info("Config file is specified, use it to overwrite default configurations");
                File file = new File(configFilePath);
                if (file.exists()) {
                    PropertiesConfiguration fileConfig = new PropertiesConfiguration(file.getAbsolutePath());
                    fileConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
                    compositeConfiguration.addConfiguration(fileConfig);
                }
            }

            compositeConfiguration.addConfiguration(new PropertiesConfiguration("config.properties"));

            return compositeConfiguration;
        } catch (ConfigurationException e) {
            LOG.fatal("Failed to load configuration", e);
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return configuration.getString(key);
    }

    public static boolean getBoolean(String key) {
        return configuration.getBoolean(key, false);
    }

}
