package org.phuongnq.chunk.fileupload.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "document")
public class DocumentProperties {

    private StorageProperties storage;
    private PresignedURLProperties presignedUrl;

    @Setter
    @Getter
    public static class StorageProperties {
        private String basePath;
        private DataSize maxFileSize;
        private DataSize fileBufferLength;
        private String[] allowedExtensions;
    }

    @Setter
    @Getter
    public static class PresignedURLProperties {
        private Duration expirationTime;
        private String secretKey;
    }
}
