package com.example.backend.dataclasses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ControllerContent {
    private String name;
    private String basePath;
    private List<RequestMapping> requestsList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class RequestMapping {
        private String method;
        private String path;
        private String returnType;
        private String methodName;
        private List<RequestParam> requestParams;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class RequestParam {
        private RequestParamType type;
        private String className;
        private String value;
    }

    public enum RequestParamType {
        PATH_VARIABLE,
        REQUEST_PARAM,
        REQUEST_BODY
    }
}
