package org.phuongnq.chunk.fileupload.service;

import lombok.RequiredArgsConstructor;
import org.phuongnq.chunk.fileupload.config.StorageProperties;
import org.phuongnq.chunk.fileupload.model.BackgroundUploadRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;

@Service
@RequiredArgsConstructor
public class FilesStorageService {
    private final StorageProperties storageProperties;

    public void store(MultipartFile file, BackgroundUploadRequest uploadRequest) throws Exception {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Failed to store empty file.");
            }

            Path storagePath = Paths.get(storageProperties.getPath());
            Path chunkStoragePath = storagePath.resolve("chunks");

            // Create chunk folder on non-existing
            chunkStoragePath.toFile().mkdir();

            Path destinationFile = chunkStoragePath
                    // Order chunk file name by padding left with zeros
                    .resolve(padLeftZeros(String.valueOf(uploadRequest.getChunkIndex()), 3))
                    .normalize()
                    .toAbsolutePath();

            if (!destinationFile.getParent().startsWith(storagePath.toAbsolutePath())) {
                // This is a security check
                throw new IllegalArgumentException("Cannot store file outside current directory.");
            }

            // Create chunk file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            DataSize currentLength = DataSize.of(
                    Math.addExact(
                            Math.multiplyExact(uploadRequest.getChunkIndex(), storageProperties.getMaximumLength().toBytes()),
                            file.getSize()),
                    DataUnit.BYTES);

            DataSize fileSize = DataSize.of(uploadRequest.getFileLength(), DataUnit.BYTES);

            if (fileSize.compareTo(currentLength) == 0) {
                // Merge files
                mergeFiles(storagePath.resolve(Paths.get(uploadRequest.getFileName())), chunkStoragePath);

                // Clean up chunks
                deleteFolder(chunkStoragePath);

                System.out.println("Finished:" + Instant.now());
            } else if (fileSize.compareTo(currentLength) < 0) {
                System.out.println("Error");
            } else {
                System.out.println("Should continue to upload file");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Failed to store file.", e);
        }
    }

    public static void mergeFiles(Path destination, Path sources) throws IOException {
        FileChannel out = FileChannel.open(destination, CREATE, WRITE);

        try (Stream<Path> stream = Files.list(sources)) {
            stream.forEach(chunk -> {
                try (FileChannel in = FileChannel.open(chunk, READ)) {
                    for (long i = 0, l = in.size(); i < l; ) {
                        i += in.transferTo(i, l - i, out);
                    }
                } catch (IOException ioException) {
                    throw new IllegalArgumentException(ioException);
                }
            });
        }

        out.close();
    }

    private void deleteFolder(Path chunkStoragePath) throws IOException {
        try (Stream<Path> pathStream = Files.walk(chunkStoragePath)) {
            pathStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    public String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);
        return sb.toString();
    }
}
