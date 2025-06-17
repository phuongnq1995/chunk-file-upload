package org.phuongnq.chunk.fileupload.controller;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phuongnq.chunk.fileupload.model.CompleteUploadRequest;
import org.phuongnq.chunk.fileupload.model.ProcessUploadRequest;
import org.phuongnq.chunk.fileupload.model.UploadRequest;
import org.phuongnq.chunk.fileupload.model.UploadResponse;
import org.phuongnq.chunk.fileupload.service.FilesStorageService;
import org.phuongnq.chunk.fileupload.service.UploadFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class FileUploadController {

  private final FilesStorageService filesStorageService;
  private final UploadFileService uploadFileService;

  @PostMapping(value = "/upload-chunk-parallel/start")
  public ResponseEntity<UploadRequest> startUploadingChunkParallel() {
    try {
      return ResponseEntity.ok(new UploadRequest(uploadFileService.createUploadChunkParallel()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }
  }

  @PostMapping(value = "/upload-chunk-parallel/complete")
  public ResponseEntity<UploadResponse> completeUploadingChunkParallel(@RequestBody CompleteUploadRequest completeUploadRequest) {
    try {
      return ResponseEntity.ok(uploadFileService.completeUploadingChunkParallel(completeUploadRequest));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }
  }

  @PostMapping(value = "/upload-chunk-parallel/process", consumes = {MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> uploadChunkWithRecovery(@RequestParam("file") MultipartFile file, ProcessUploadRequest processUploadRequest) {
    try {
      log.info("{},uploadRequest:{}", Instant.now(), processUploadRequest);
      log.info("Size:{}", file.getSize());
      uploadFileService.processUploadingChunkParallel(file, processUploadRequest);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }
  }

  @PostMapping(value = "/upload-chunk-override", consumes = {MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file,
      ProcessUploadRequest processUploadRequest) {
    try {
      log.info("{},uploadRequest:{}", Instant.now(), processUploadRequest);
      log.info("Size:{}", file.getSize());
      filesStorageService.storeChunkOverride(file, processUploadRequest);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }
  }

}
