package com.example.backend.dataclasses;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ClassContent {
    private String name;
    private List<ClassValue> values;

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ClassValue {
        private String type;
        private String name;
    }
}