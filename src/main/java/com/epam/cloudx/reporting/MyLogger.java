package com.epam.cloudx.reporting;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MyLogger {
  private static final Logger LOGGER = LogManager.getLogger(MyLogger.class);

  public static void info(String message) {
    LOGGER.info(message);
  }

  public static void error(String message) {
    LOGGER.error(message);
  }

  public static void debug(String message) {
    LOGGER.debug(message);
  }
}
