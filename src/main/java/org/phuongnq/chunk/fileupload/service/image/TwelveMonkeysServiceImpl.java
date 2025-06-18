package org.phuongnq.chunk.fileupload.service.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwelveMonkeysServiceImpl implements ImageHandlerService {

  @Override
  public void cropImage(InputStream image, File croppedFile, Number x, Number y, Number width, Number height) {
    try {

      BufferedImage originalImage = ImageIO.read(image);
      int originalWidth = originalImage.getWidth();
      int originalHeight = originalImage.getHeight();
      log.info("Original size: {}x{} ", originalWidth, originalHeight);

      BufferedImage cropped = originalImage.getSubimage(x.intValue(), y.intValue(), width.intValue(), height.intValue());
      ImageIO.write(cropped, "jpg", croppedFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getServiceName() {
    return "twelvemonkeys";
  }
}
