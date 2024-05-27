package com.example.backend.service;

import com.example.backend.dataclasses.ClassContent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
public class FrontendGeneratorService {

    public String getTSFile(MultipartFile file) {
        ClassContent classInformation = getClassFromFile(getFileContent(file));
        return getTSFileFromContent(classInformation);
    }

    private static String getTSFileFromContent(ClassContent content) {
        StringBuilder tsContent = new StringBuilder("export interface " + content.getName() + " {\n");
        content.getValues().forEach(v -> {
            tsContent.append("  ").append(v.getName()).append(": ").append(convertJavaTypeToTSType(v.getType())).append("\n");
        });
        tsContent.append("}");
        copyToClipboard(tsContent.toString());
        return tsContent.toString();
    }

    private static String convertJavaTypeToTSType(String s) {
        switch (s) {
            case "String": return "string";
            case "int", "Integer", "double" : return "number";
            case "boolean", "Boolean": return "boolean";
            case "Instant": return "Date";
            default: {
                if (s.contains("List<")) {
                    String sNoList = s.substring(0, s.length() - 1).replace("List<", "");
                    return sNoList + "[]";
                }
                else {
                    return s;
                }
            }
        }
    }

    private static ClassContent getClassFromFile(String content) {
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

    private static String getFileContent(MultipartFile file) {
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

    private static void copyToClipboard(String content) {
        StringSelection selection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}