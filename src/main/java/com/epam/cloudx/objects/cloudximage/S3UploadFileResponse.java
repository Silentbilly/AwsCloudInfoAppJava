package com.epam.cloudx.objects.cloudximage;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;

public record S3UploadFileResponse(Integer id,
                                   @JsonProperty("last_modified") Timestamp lastModified,
                                   @JsonProperty("object_key") String objectKey,
                                   @JsonProperty("object_size") Integer objectSize,
                                   @JsonProperty("object_type") String objectType) {

}
