package org.phuongnq.chunk.fileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ChunkFileUploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChunkFileUploadApplication.class, args);
	}

}
