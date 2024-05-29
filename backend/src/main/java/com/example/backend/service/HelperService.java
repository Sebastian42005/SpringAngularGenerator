package com.example.backend.service;

import com.example.backend.dataclasses.ClassContent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HelperService {

    public ClassContent getClassFromFile(String content) {
        ClassContent classContent = new ClassContent();
        List<String> lines = Arrays.stream(content.split("\n")).toList();
        for (String line : lines) {
            if (line.contains("class")) {
                List<String> words = Arrays.asList(line.split(" "));
                String className = words.get(words.size() - 2);
                classContent.setName(className);
            }
            if (line.contains("private")) {
                List<String> words = Arrays.asList(line
                        .replace("private", "")
                        .replace(";", "").split(" "));
                String value = words.getLast();
                String type = words.get(words.size() - 2);
                classContent.getValues().add(new ClassContent.ClassValue(type, value));
            }
        }
        return classContent;
    }

    public String getFileContent(MultipartFile file) {
        String content = "";
        try {
            content = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return "Fehler beim Lesen der Datei";
        }
        return content;
    }

    public String getFileContent(String file) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            StringBuilder content = new StringBuilder();
            for (String line : lines) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException exception) {
            System.out.println("File mit dem namen " + file + " gibt es nicht!");
            return "";
        }
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public String getFilePath(String fileName) {
        return "backend/src/main/java/com/example/backend/files/" + fileName + ".txt";
    }

    public String replaceFileContent(String content, String replacement) {
        content = content
                .replace("Replacements", changeWordToMultiWord(replacement))
                .replace("Replacement", replacement)
                .replace("replacement", replacement.toLowerCase());
        return content;
    }

    public String changeWordToMultiWord(String word) {
        if (word.equals("person")) {
            return "people";
        }else if (word.endsWith("s")) {
            return word + "es";
        }
        if (word.endsWith("y")) {
            return word.substring(0, word.length() - 1) + "ies";
        }
        return word + "s";
    }
}
