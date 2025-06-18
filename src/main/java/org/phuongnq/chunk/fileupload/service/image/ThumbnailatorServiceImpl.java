package org.phuongnq.chunk.fileupload.service.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

@Service
public class ThumbnailatorServiceImpl implements ImageHandlerService {

  @Override
  public void cropImage(InputStream image, File croppedFile, Number x, Number y, Number width, Number height) {
    try {

      /*BufferedImage originalImage = ImageIO.read(image);
      int originalWidth = originalImage.getWidth();
      int originalHeight = originalImage.getHeight();*/

      Thumbnails.of(image).sourceRegion(x.intValue(), y.intValue(), width.intValue(), height.intValue())
          .size(width.intValue() - x.intValue(), height.intValue() - y.intValue())
          .toFile(croppedFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getServiceName() {
    return "thumbnailator";
  }
}
