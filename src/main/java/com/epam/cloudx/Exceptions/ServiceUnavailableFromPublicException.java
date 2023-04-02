package com.epam.cloudx.Exceptions;

import java.net.UnknownHostException;

public class ServiceUnavailableFromPublicException extends UnknownHostException {

  public ServiceUnavailableFromPublicException(String message) {
    super(message);
  }
}
