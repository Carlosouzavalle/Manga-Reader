package com.manga.mangareader;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    private final DriveServices drive;

    public ImageController(DriveServices drive) {
        this.drive = drive;
    }

    @GetMapping("/img/{id}")
    public ResponseEntity<byte[]> img(@PathVariable String id) {
        ResponseEntity<byte[]> res = drive.fetchImage(id);
        return ResponseEntity.ok()
            .contentType(res.getHeaders().getContentType())
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
            .body(res.getBody());
    }
}