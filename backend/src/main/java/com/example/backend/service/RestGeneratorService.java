package com.example.backend.service;

import com.example.backend.dataclasses.ClassContent;
import com.example.backend.enums.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestGeneratorService {

    private final HelperService helperService;

    public String generateRest(String name, Option option) {
        return helperService.replaceFileContent(helperService.getFileContent(helperService.getFilePath("Name" + option.getName())), name);
    }

    public String generateService(MultipartFile file) {
        ClassContent classContent = helperService.getClassFromFile(helperService.getFileContent(file));
        List<String> updates = new ArrayList<>();
        classContent.getValues().forEach(value -> {
            if (!value.getName().equals("id")) {
                String name = helperService.capitalizeFirstLetter(value.getName());
                updates.add("dbReplacement.set" + name + "(replacement.get" + name + "());");
            }
        });
        String content = helperService.getFileContent(helperService.getFilePath("NameService"));
        content = content.replace("//TODO: implement update", String.join("\n        ", updates));
        return helperService.replaceFileContent(content, classContent.getName());
    }
}