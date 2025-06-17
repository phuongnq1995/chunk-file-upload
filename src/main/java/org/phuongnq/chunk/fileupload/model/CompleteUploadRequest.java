package org.phuongnq.chunk.fileupload.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompleteUploadRequest {
  private String uploadId;
  private String fileName;
}
