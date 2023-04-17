package com.epam.cloudx.endpoints;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiGateEndpoints {
  S3_BUCKET_UPLOAD_IMAGE("api/image"),
  S3_BUCKET_GET_ALL_IMAGES_METADATA("api/image"),
  S3_BUCKET_GET_IMAGE_BY_ID("api/image/file/{image_id}"),
  S3_BUCKET_DELETE_IMAGE_BY_ID("api/image/{image_id}"),
  S3_BUCKET_GET_IMAGE_INFO_BY_ID("api/image/{image_id}");
  private final String value;
}

