package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.dto.BinaryObject;
import com.zebrunner.reporting.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("v1/assets")
@RequiredArgsConstructor
public class AssetsController {

    private final StorageService storageService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @SneakyThrows(IOException.class)
    public Map<String, String> saveAsset(@RequestParam("type") BinaryObject.Type type,
                                         @RequestParam("file") MultipartFile file) {
        BinaryObject binaryObject = BinaryObject.builder()
                                                .type(type)
                                                .name(file.getOriginalFilename())
                                                .contentType(file.getContentType())
                                                .data(file.getInputStream())
                                                .size(file.getSize())
                                                .build();
        String key = storageService.save(binaryObject);
        return Collections.singletonMap("key", key);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAsset(@RequestParam("key") String key) {
        storageService.removeObject(key);
    }

}
