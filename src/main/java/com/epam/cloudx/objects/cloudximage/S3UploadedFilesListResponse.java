package com.epam.cloudx.objects.cloudximage;

import java.util.List;

public record S3UploadedFilesListResponse(List<S3UploadFileResponse> s3UploadFileResponses) {

}
