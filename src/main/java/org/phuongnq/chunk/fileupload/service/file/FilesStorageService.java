package org.phuongnq.chunk.fileupload.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phuongnq.chunk.fileupload.config.DocumentProperties;
import org.phuongnq.chunk.fileupload.model.ProcessUploadRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilesStorageService {
    private final DocumentProperties documentProperties;

    public void storeChunkOverride(MultipartFile file, ProcessUploadRequest processUploadRequest) throws Exception {
        /*try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Failed to store empty file.");
            }

            Path storagePath = Paths.get(documentProperties.getStorage().getBasePath());
            Path overrideStoragePath = storagePath.resolve("overrides");

            // Create chunk folder on non-existing
            overrideStoragePath.toFile().mkdir();

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
            throw new Exception("Failed to store file.", e);
        }*/
    }
}
