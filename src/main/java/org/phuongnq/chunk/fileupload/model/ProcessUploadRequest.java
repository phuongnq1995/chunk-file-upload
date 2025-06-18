package org.phuongnq.chunk.fileupload.model;

import lombok.Data;

@Data
public class ProcessUploadRequest extends UploadRequest {
  private String fileName;
  private long fileLength;
  private int chunkIndex;
  private long chunkLength;
}
