package com.example.backend.dataclasses;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ClassContent {
    private String name;
    private List<ClassValue> values;

    public ClassContent() {
        this.values = new ArrayList<>();
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ClassValue {
        private String type;
        private String name;
    }
}