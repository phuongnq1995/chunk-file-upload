package org.phuongnq.chunk.fileupload.runner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;
import org.phuongnq.chunk.fileupload.config.DocumentProperties;
import org.phuongnq.chunk.fileupload.service.image.ImageHandlerService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageTestRunner implements ApplicationRunner {

  private final DocumentProperties documentProperties;
  private final ResourceLoader resourceLoader;
  private final List<ImageHandlerService> imageHandlerServices;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    Resource resource = resourceLoader.getResource("classpath:images/5000x3000.jpg");

    ImageInfo imageInfo = Imaging.getImageInfo(resource.getFile());
    log.info("Original DPI: {}", imageInfo.getPhysicalWidthDpi());

    for (ImageHandlerService imageHandlerService : imageHandlerServices) {

      File outputFile = Path.of(documentProperties.getStorage().getBasePath())
          .resolve(imageHandlerService.getServiceName() + "-medium.jpg")
          .toFile();

      try {
        Instant now = Instant.now();
        log.info("Processing {}", imageHandlerService.getServiceName());
        imageHandlerService.cropImage(resource.getInputStream(), outputFile, 700, 700, 1500, 900);
        log.info("Finished in {} ms", Instant.now().toEpochMilli() - now.toEpochMilli());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
