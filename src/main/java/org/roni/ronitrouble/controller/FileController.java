package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.service.FileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @GetMapping
    public FileService.FileUrlInfo getFileUrlInfoBeforeUpload(@RequestParam("rawFileName") String rawFileName, @RequestParam("Category") String category) {
        return fileService.generateFileUploadUrl(rawFileName, category);
    }

}
