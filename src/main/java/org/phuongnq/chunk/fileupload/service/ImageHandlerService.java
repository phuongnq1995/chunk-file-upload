package org.phuongnq.chunk.fileupload.service;

import java.io.File;
import java.io.InputStream;
import org.apache.commons.io.FilenameUtils;

public interface ImageHandlerService {

  void cropImage(InputStream image, File croppedFile, Number x, Number y, Number width, Number height);

  String getServiceName();

  default String getExtension(File file) {
    return FilenameUtils.getExtension(file.getName());
  }
}
