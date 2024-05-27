package com.example.backend.service;

import com.example.backend.enums.Option;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RestGeneratorService {

    public String generateRest(String name, Option option) {
        return printFileContentWithReplacement(option.getName(), name);
    }

    private static String printFileContentWithReplacement(String option, String replacement) {
        String content = getFileContent("backend/src/main/java/com/example/backend/files/Name" + option + ".txt");
        content = content
                .replace("Replacements", changeLastYToIes(replacement))
                .replace("Replacement", replacement)
                .replace("replacement", replacement.toLowerCase());
        return content;
    }

    public static String changeLastYToIes(String word) {
        if (word.endsWith("y")) {
            return word.substring(0, word.length() - 1) + "ies";
        }
        return word;
    }

    private static String getFileContent(String file) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            StringBuilder content = new StringBuilder();
            for (String line : lines) {
                content.append(line).append("\n");
            }
            return content.toString();
        }catch (IOException exception) {
            System.out.println("Gibt es nicht!");
            return "";
        }
    }
}