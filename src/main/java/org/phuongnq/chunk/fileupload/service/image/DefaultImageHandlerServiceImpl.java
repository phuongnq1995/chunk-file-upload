package org.phuongnq.chunk.fileupload.service.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultImageHandlerServiceImpl implements ImageHandlerService {

  @Override
  public void cropImage(InputStream image, File croppedFile, Number x, Number y, Number width, Number height) {
    try {
      BufferedImage originalImage = ImageIO.read(image);

      dpiChecking(originalImage);

      int originalWidth = originalImage.getWidth();
      int originalHeight = originalImage.getHeight();

      log.info("Original size: {}x{} ", originalImage.getWidth(), originalImage.getHeight());

      BufferedImage croppedImage = originalImage.getSubimage(x.intValue(), y.intValue(), width.intValue(),
          height.intValue());

      Image scaledImage = croppedImage.getScaledInstance(originalWidth, originalHeight, Image.SCALE_SMOOTH);
      BufferedImage bufferedScaledImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_RGB);
      bufferedScaledImage.getGraphics().drawImage(scaledImage, 0, 0, null);

      dpiChecking(bufferedScaledImage);

      ImageIO.write(bufferedScaledImage, "jpg", croppedFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getServiceName() {
    return "default";
  }

  public void dpiChecking(BufferedImage bufferedImage) {
    int widthPixels = bufferedImage.getWidth();
    double widthInches = 8.5; // assume or measure
    double dpi = widthPixels / widthInches;

    log.info("dpi: {}", dpi);
  }
}
