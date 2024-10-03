package org.phuongnq.chunk.fileupload.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
    private String path;
    private DataSize maximumLength;
}
