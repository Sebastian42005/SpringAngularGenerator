package com.example.backend.controller;

import com.example.backend.dto.FileDto;
import com.example.backend.dto.FileListDto;
import com.example.backend.dto.FrontendFileListDto;
import com.example.backend.enums.Option;
import com.example.backend.service.FrontendGeneratorService;
import com.example.backend.service.RestGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/frontend")
@RequiredArgsConstructor
public class FrontendGeneratorController {

    private final FrontendGeneratorService frontendGeneratorService;

    @PostMapping("/ts-file")
    public List<FileDto> generateTsFile(@RequestParam("files") List<MultipartFile> files) {
        return frontendGeneratorService.getTSFile(files);
    }

    @PostMapping("/admin-page")
    public List<FrontendFileListDto> generateAdminPage(@RequestParam("files") List<MultipartFile> files) {
        List<FrontendFileListDto> restGeneratorList = new ArrayList<>();
        files.forEach(file -> {
            String fileName = Objects.requireNonNull(file.getOriginalFilename()).replace(".java", "");
            restGeneratorList.add(new FrontendFileListDto(fileName, frontendGeneratorService.getAdminPage(file)));
        });
        return restGeneratorList;
    }

    @PostMapping("/api-calls")
    public FileDto generateApiCalls(@RequestParam("files") List<MultipartFile> files) {
        List<String> apiCalls = new ArrayList<>();
        files.forEach(file -> {
            apiCalls.add(frontendGeneratorService.getApiCalls(file));
        });
        return new FileDto("API", String.join("\n\n", apiCalls));
    }
}
