package com.epam.cloudx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InstanceTypes {
  PRIVATE("Private"),
  PUBLIC("Public");
  private final String value;
}

