package com.example.backend.controller;

import com.example.backend.dto.RestGeneratorDto;
import com.example.backend.enums.Option;
import com.example.backend.service.RestGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend")
@RequiredArgsConstructor
public class RestGeneratorController {

    private final RestGeneratorService restGeneratorService;

    @GetMapping("/generate/{name}")
    public List<RestGeneratorDto> generateRest(@PathVariable String name) {
        return List.of(
                new RestGeneratorDto("Controller", restGeneratorService.generateRest(name, Option.CONTROLLER)),
                new RestGeneratorDto("Service", restGeneratorService.generateRest(name, Option.SERVICE)),
                new RestGeneratorDto("Repository", restGeneratorService.generateRest(name, Option.REPOSITORY)),
                new RestGeneratorDto("Entity", restGeneratorService.generateRest(name, Option.ENTITY))
        );
    }
}
