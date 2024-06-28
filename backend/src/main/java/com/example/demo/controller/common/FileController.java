package com.example.demo.controller.common;

import com.example.demo.backup.ExcelService;
import com.example.demo.service.common.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {
    private final FileService service;

    @GetMapping("/download/{id}")
    public ResponseEntity<?> fileDownload(@PathVariable Long id) {
        Map<String, Object> res = service.downloadFile(id);
        Resource file = (Resource) res.get("file");
        String contentDisposition = (String) res.get("contentDisposition");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(file);
    }

}
