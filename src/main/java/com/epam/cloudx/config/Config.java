package com.epam.cloudx.config;

import static com.epam.cloudx.config.Constants.ACCESS_KEY;
import static com.epam.cloudx.config.Constants.API_URL;
import static com.epam.cloudx.config.Constants.SECRET_KEY;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import lombok.SneakyThrows;

//@Log4j
public class Config {

  private static Config config;
  private final Properties props;

  private Config() {
    props = new Properties();
    loadProperties();
  }

  public static Config getInstance() {
    if (config == null) {
      config = new Config();
    }
    return config;
  }

  public String getAccessKey() {
    return props.getProperty(ACCESS_KEY);
  }

  public String getSecretKey() {
    return props.getProperty(SECRET_KEY);
  }

  @SneakyThrows
  private void loadProperties() {
    //log.info("Loading of configuration file");
    try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("qa.properties")) {
      props.load(is);
    }

    //log.info("Loading from environmental variables");
    props.keySet().forEach(property -> {
      String systemProperty = System.getProperty(property.toString());
      if (Objects.nonNull(systemProperty)) {
        props.setProperty(property.toString(), systemProperty);
      }
    });
  }
}
