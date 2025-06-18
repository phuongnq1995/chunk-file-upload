package org.phuongnq.chunk.fileupload.service.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phuongnq.chunk.fileupload.config.DocumentProperties;
import org.phuongnq.chunk.fileupload.model.CompleteUploadRequest;
import org.phuongnq.chunk.fileupload.model.ProcessUploadRequest;
import org.phuongnq.chunk.fileupload.model.UploadResponse;
import org.phuongnq.chunk.fileupload.utils.FileHandlerUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParallelUploadFileService {

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

  public void processUploadingChunkParallel(MultipartFile file, ProcessUploadRequest processUploadRequest)
      throws IOException {
    try {
      if (file.isEmpty()) {
        throw new IllegalArgumentException("Failed to store empty file.");
      }

      Path storagePath = Paths.get(documentProperties.getStorage().getBasePath());
      Path chunkStoragePath = storagePath.resolve(processUploadRequest.getUploadId()).resolve("chunks");

      // Create chunk folder on non-existing
      chunkStoragePath.toFile().mkdir();

      Path destinationFile = chunkStoragePath
          // Order chunk file name by padding left with zeros
          .resolve(FileHandlerUtils.padLeftZeros(String.valueOf(processUploadRequest.getChunkIndex()), 3)).normalize()
          .toAbsolutePath();

      if (!destinationFile.getParent().startsWith(storagePath.toAbsolutePath())) {
        // This is a security check
        throw new IllegalArgumentException("Cannot store file outside current directory.");
      }

      // Create chunk file
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
      }

    } catch (IOException e) {
      log.error("Error", e);
      throw new IOException("Failed to store file.", e);
    }
  }

  public UploadResponse completeUploadingChunkParallel(CompleteUploadRequest uploadRequest) throws Exception {
    String uploadId = uploadRequest.getUploadId();
    Path storagePath = Paths.get(documentProperties.getStorage().getBasePath());
    Path uploadPath = storagePath.resolve(uploadRequest.getUploadId());
    Path chunkStoragePath = uploadPath.resolve("chunks");

    log.info("Getting all chunks, starting merge chunks at: {}", Instant.now());
    // Merge files
    FileHandlerUtils.mergeFiles(uploadPath.resolve(Paths.get(uploadRequest.getFileName())), chunkStoragePath);

    // Clean up chunks
    FileHandlerUtils.deleteFolder(chunkStoragePath);

    log.info("Finished merging chunks at:{}", Instant.now());

    fileUploadStatus.put(uploadId, "COMPLETED");

    UploadResponse uploadResponse = new UploadResponse();
    uploadResponse.setUploadId(uploadId);
    uploadResponse.setFileName(uploadRequest.getFileName());
    // TODO: Update download url
    uploadResponse.setDownloadUrl(uploadPath.resolve(Paths.get(uploadRequest.getFileName())).toString());
    uploadResponse.setStatus(fileUploadStatus.get(uploadId));

    return uploadResponse;
  }
}
