package org.phuongnq.chunk.fileupload.model;

import lombok.Data;

@Data
public class BackgroundUploadRequest {
    private long fileLength;
    private String fileName;
    private int chunkIndex;
}
