package com.example.backend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Option {
    CONTROLLER("Controller"),
    SERVICE("Service"),
    REPOSITORY("Repository"),
    ENTITY("Entity");

    private final String name;
}
