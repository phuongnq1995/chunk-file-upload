package org.phuongnq.chunk.fileupload.service.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phuongnq.chunk.fileupload.config.DocumentProperties;
import org.phuongnq.chunk.fileupload.model.ProcessUploadRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class OverrideUploadFileService {

  private final ConcurrentHashMap<String, String> fileUploadStatus = new ConcurrentHashMap<>();
  private final DocumentProperties documentProperties;

  /**
   * Generate upload id
   *
   * @return upload id
   */
  public String createUploadChunkParallel() {
    String uploadId = UUID.randomUUID().toString();

    Path storagePath = Paths.get(documentProperties.getStorage().getBasePath());
    Path uploadStoragePath = storagePath.resolve(uploadId);
    uploadStoragePath.toFile().mkdir();

    fileUploadStatus.put(uploadId, "CREATED");
    return uploadId;
  }

  public void processUploadingChunkOverride(MultipartFile file, ProcessUploadRequest processUploadRequest)
      throws IOException {
    try {
      if (file.isEmpty()) {
        throw new IllegalArgumentException("Failed to store empty file.");
      }

      Path storagePath = Paths.get(documentProperties.getStorage().getBasePath());
      Path overrideStoragePath = storagePath.resolve(processUploadRequest.getUploadId());

      Path destinationPath = overrideStoragePath.resolve(Paths.get(processUploadRequest.getFileName()));
      File destinationFile = destinationPath.toFile();

      try (ReadableByteChannel inputChannel = Channels.newChannel(file.getInputStream());
          FileOutputStream fos = new FileOutputStream(destinationFile, true);
          FileChannel fileChannel = fos.getChannel()) {

        // Allocate a buffer
        ByteBuffer buffer = ByteBuffer.allocateDirect(
            (int) documentProperties.getStorage().getFileBufferLength().toBytes());

        // Read from the InputStream and write to the FileChannel
        while (inputChannel.read(buffer) != -1) {
          buffer.flip(); // Prepare the buffer for writing
          while (buffer.hasRemaining()) {
            fileChannel.write(buffer);
          }
          buffer.clear(); // Prepare the buffer for the next read
        }
      }

      DataSize currentLength = DataSize.ofBytes(destinationFile.length());

      if (processUploadRequest.getFileLength() == currentLength.toBytes()) {
        log.info("Finished upload chunks at: {}", Instant.now());
      } else if (currentLength.toBytes() > processUploadRequest.getFileLength()) {
        log.error("Error");
        throw new IllegalArgumentException("Error");
      } else {
        log.info("Should continue to upload file");
      }
    } catch (IOException e) {
      log.error("Error", e);
      throw e;
    }
  }
}
