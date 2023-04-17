package com.epam.cloudx.endpoints;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiGateEndpoints {
  S3_BUCKET_UPLOAD_IMAGE("api/image"),
  S3_BUCKET_GET_ALL_IMAGES_METADATA("api/image");
  private final String value;
}

