package com.example.backend.controller;

import com.example.backend.dto.FileDto;
import com.example.backend.dto.FileListDto;
import com.example.backend.enums.Option;
import com.example.backend.service.RestGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/backend")
@RequiredArgsConstructor
public class RestGeneratorController {

    private final RestGeneratorService restGeneratorService;

    @GetMapping("/generate/{name}")
    public FileDto generateRest(@PathVariable String name) {
        return new FileDto("Entity", restGeneratorService.generateRest(name, Option.ENTITY));
    }

    @PostMapping("/generate")
    public List<FileListDto> generateRest(@RequestParam("files") List<MultipartFile> files) {
        List<FileListDto> restGeneratorList = new ArrayList<>();
        files.forEach(file -> {
            String fileName = Objects.requireNonNull(file.getOriginalFilename()).replace(".java", "");
            List<FileDto> generatorDtoList = List.of(
                    new FileDto("Repository", restGeneratorService.generateRest(fileName, Option.REPOSITORY)),
                    new FileDto("Service", restGeneratorService.generateService(file)),
                    new FileDto("Controller", restGeneratorService.generateRest(fileName, Option.CONTROLLER))
            );
            restGeneratorList.add(new FileListDto(fileName, generatorDtoList));
        });
        return restGeneratorList;
    }
}
