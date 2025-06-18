package org.phuongnq.chunk.fileupload.controller;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phuongnq.chunk.fileupload.model.CompleteUploadRequest;
import org.phuongnq.chunk.fileupload.model.ProcessUploadRequest;
import org.phuongnq.chunk.fileupload.model.UploadRequest;
import org.phuongnq.chunk.fileupload.model.UploadResponse;
import org.phuongnq.chunk.fileupload.service.file.ParallelUploadFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/upload-chunk-parallel")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ParallelFileUploadController {

  private final ParallelUploadFileService parallelUploadFileService;

  @PostMapping(value = "/start")
  public ResponseEntity<UploadRequest> startUploadingChunkParallel() {
    try {
      return ResponseEntity.ok(new UploadRequest(parallelUploadFileService.createUploadChunkParallel()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }
  }

  @PostMapping(value = "/complete")
  public ResponseEntity<UploadResponse> completeUploadingChunkParallel(@RequestBody CompleteUploadRequest completeUploadRequest) {
    try {
      return ResponseEntity.ok(parallelUploadFileService.completeUploadingChunkParallel(completeUploadRequest));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }
  }

  @PostMapping(value = "/process", consumes = {MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> uploadChunkWithRecovery(@RequestParam("file") MultipartFile file, ProcessUploadRequest processUploadRequest) {
    try {
      log.info("{},uploadRequest:{}", Instant.now(), processUploadRequest);
      log.info("Size:{}", file.getSize());
      parallelUploadFileService.processUploadingChunkParallel(file, processUploadRequest);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }
  }
}
