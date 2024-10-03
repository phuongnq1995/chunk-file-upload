package org.phuongnq.chunk.fileupload.controller;

import lombok.RequiredArgsConstructor;
import org.phuongnq.chunk.fileupload.model.BackgroundUploadRequest;
import org.phuongnq.chunk.fileupload.service.FilesStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class FileUploadController {
    private final FilesStorageService filesStorageService;

    @PostMapping(value = "/upload", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file, BackgroundUploadRequest uploadRequest) {
        try {
            System.out.println(Instant.now() + ",uploadRequest:" + uploadRequest);
            System.out.println("M:" + file.getSize());
            filesStorageService.store(file, uploadRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }
}
