package com.candescent.banking.niis.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class fileLoader {
    public static Properties loadProperties(String filePath) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(filePath);
        props.load(fis);
        return props;

    }
}