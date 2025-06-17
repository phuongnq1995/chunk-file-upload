package org.phuongnq.chunk.fileupload.utils;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileHandlerUtils {

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

  public static void deleteFolder(Path chunkStoragePath) throws IOException {
    try (Stream<Path> pathStream = Files.walk(chunkStoragePath)) {
      pathStream.sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }
  }

  public static String padLeftZeros(String inputString, int length) {
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
