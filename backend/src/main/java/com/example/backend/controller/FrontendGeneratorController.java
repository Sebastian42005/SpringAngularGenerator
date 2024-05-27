package com.example.backend.controller;

import com.example.backend.service.FrontendGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/frontend")
@RequiredArgsConstructor
public class FrontendGeneratorController {

    private final FrontendGeneratorService frontendGeneratorService;

    @PostMapping("/ts-file")
    public String generateTsFile(@RequestParam("file") MultipartFile file) {
        return frontendGeneratorService.getTSFile(file);
    }
}
