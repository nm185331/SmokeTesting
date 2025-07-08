package com.ncr.banking.niis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    public static Properties loadConfig(String env) throws IOException {
        String fileName = "config-"+env + ".properties";

        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Config file not found: " + fileName);
            }
            Properties props = new Properties();
            props.load(input);
            return props;
        }
    }
}