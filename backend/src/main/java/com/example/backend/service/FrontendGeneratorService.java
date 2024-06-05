package com.example.backend.service;

import com.example.backend.dataclasses.ClassContent;
import com.example.backend.dataclasses.ControllerContent;
import com.example.backend.dto.FileDto;
import com.example.backend.dto.FileListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FrontendGeneratorService {

    private final HelperService helperService;
    private final List<String> dataTypes = List.of("String", "Integer", "int", "Boolean", "boolean", "Date", "Instant", "double", "float", "Role");

    public List<FileListDto> getAdminPage(MultipartFile multipartFile) {
        String fileContent = helperService.getFileContent(multipartFile);
        ClassContent classContent = helperService.getClassFromFile(fileContent);
        return List.of(new FileListDto("Home", List.of(
                new FileDto("HTML", generateAdminHtmlComponent(classContent)),
                new FileDto("TS", generateAdminTsComponent(classContent)),
                new FileDto("SCSS", generateAdminScssComponent(classContent))
        )), new FileListDto("Create", List.of(
                new FileDto("HTML", generateAdminCreateHtmlComponent(classContent)),
                new FileDto("TS", generateAdminCreateTsComponent(classContent)),
                new FileDto("SCSS", generateAdminCreateScssComponent())
        )), new FileListDto("Detail", List.of(
                new FileDto("HTML", generateAdminDetailHtmlComponent(classContent)),
                new FileDto("TS", generateAdminDetailTSComponent(classContent)),
                new FileDto("SCSS", generateAdminDetailScssComponent(classContent))
        )));
    }

    public String getApiCalls(MultipartFile multipartFile) {
        String fileContent = helperService.getFileContent(multipartFile);
        ClassContent classContent = helperService.getClassFromFile(fileContent);
        return "  //" + Objects.requireNonNull(multipartFile.getOriginalFilename()).replace(".java", "") + "\n\n" + generateAPICalls(classContent);
    }

    private String generateAdminHtmlComponent(ClassContent classContent) {
        String content = helperService.getFileContent(helperService.getFilePath("FrontendAdminHtmlComponent"));
        List<String> tableColumns = new ArrayList<>();
        classContent.getValues().forEach(value -> {
            String column = " <!-- Replacement Column -->\n" +
                    "      <ng-container matColumnDef=\"replacement\">\n" +
                    "        <th mat-header-cell *matHeaderCellDef mat-sort-header> Replacement </th>\n" +
                    "        <td mat-cell *matCellDef=\"let row\"> {{ row.replacementtypeRep}}</td>\n" +
                    "      </ng-container>";
            String name = helperService.capitalizeFirstLetter(value.getName());
            column = column
                    .replace("typeRep", getTypeDisplay(value.getType()))
                    .replace("Replacement", name)
                    .replace("replacement", value.getName());
            tableColumns.add(column);
        });
        return helperService.replaceFileContent(content.replace("//TODO Columns", String.join("\n\n      ", tableColumns)), classContent.getName());
    }

    private String getTypeDisplay(String type) {
        if (!dataTypes.contains(type)) {
            return "?.id";
        }
        return switch (type) {
            case "Instant" -> " | date:'dd.MM.yyyy HH:mm'";
            case "Date" -> " | date:'dd.MM.yyyy'";
            default -> "";
        };
    }

    private String generateAdminScssComponent(ClassContent classContent) {
        String content = helperService.getFileContent(helperService.getFilePath("FrontendAdminScssComponent"));
        return helperService.replaceFileContent(content, classContent.getName());
    }

    private String generateAPICalls(ClassContent classContent) {
        String content = helperService.getFileContent(helperService.getFilePath("FrontendAPICalls"));
        return helperService.replaceFileContent(content, classContent.getName());
    }

    private String generateAdminTsComponent(ClassContent classContent) {
        String content = helperService.getFileContent(helperService.getFilePath("FrontendAdminTsComponent"));
        List<String> nameList = classContent.getValues().stream().map(value -> "'" + value.getName() + "'").toList();
        return helperService.replaceFileContent(content.replace("//TODO Column", String.join(", ", nameList)), classContent.getName());
    }

    private String generateAdminCreateHtmlComponent(ClassContent classContent) {
        String content = helperService.getFileContent(helperService.getFilePath("FrontendAdminCreateHtmlComponent"));
        List<String> inputFields = new ArrayList<>();
        classContent.getValues().forEach(value -> {
            if (!value.getName().equalsIgnoreCase("id")) {
                String inputField = "   <mat-form-field>\n" +
                        "      <mat-label>ReplacementColumn</mat-label>\n" +
                        "      <input matInput type=\"typeRep\" [(ngModel)]=\"replacement.replacementColumn\" name=\"replacementColumn\">\n" +
                        "    </mat-form-field>";
                String dropDownMenu = "    <mat-form-field>\n" +
                        "      <mat-label>ReplacementColumn</mat-label>\n" +
                        "      <mat-select [(ngModel)]=\"replacement.replacementColumn\" name=\"replacementColumn\">\n" +
                        "        <mat-option *ngFor=\"let replacementColumn of replacementColumns\" [value]=\"replacementColumn\">\n" +
                        "          {{replacementColumn.id}}\n" +
                        "        </mat-option>\n" +
                        "      </mat-select>\n" +
                        "    </mat-form-field>";
                String name = helperService.capitalizeFirstLetter(value.getName());
                if (dataTypes.contains(value.getType())) {
                    inputField = inputField
                            .replace("ReplacementColumn", name)
                            .replace("replacementColumn", value.getName())
                            .replace("typeRep", getInputFieldType(value.getType()));
                    inputFields.add(inputField);

                } else if (!value.getType().startsWith("List<")) {
                    dropDownMenu = dropDownMenu
                            .replace("replacementColumns", helperService.changeWordToMultiWord(name.toLowerCase()))
                            .replace("ReplacementColumn", name)
                            .replace("replacementColumn", value.getName());
                    inputFields.add(dropDownMenu);
                }
            }
        });
        return helperService.replaceFileContent(content.replace("//TODO Input Fields", String.join("\n\n      ", inputFields)), classContent.getName());
    }

    private String generateAdminCreateTsComponent(ClassContent classContent) {
        String content = helperService.getFileContent(helperService.getFilePath("FrontendAdminCreateTsComponent"));
        List<String> entityList = new ArrayList<>();
        List<String> apiCalls = new ArrayList<>();
        classContent.getValues().forEach(value -> {
            if (!dataTypes.contains(value.getType()) && !value.getType().startsWith("List<")) {
                String valueName = helperService.capitalizeFirstLetter(value.getName());
                entityList.add(helperService.changeWordToMultiWord(value.getName()) + ": " + valueName + "[]");

                String apiCall = "    this.apiService.getAllReplacementColumns().subscribe(replacementColumns => {\n" +
                        "      this.replacementColumns = replacementColumns;\n" +
                        "      if (this.edit && this.replacement.replacementColumn) {\n" +
                        "        const foundReplacementColumn = this.replacementColumns.find(\n" +
                        "          replacementColumn => replacementColumn.id === this.replacement.replacementColumn?.id\n" +
                        "        );\n" +
                        "        if (foundReplacementColumn) {\n" +
                        "          this.replacement.replacementColumn = foundReplacementColumn;\n" +
                        "        }\n" +
                        "      }\n" +
                        "    });";
                apiCall = apiCall
                        .replace("replacementColumns", helperService.changeWordToMultiWord(value.getName()))
                        .replace("ReplacementColumns", helperService.changeWordToMultiWord(valueName))
                        .replace("replacementColumn", value.getName());
                apiCalls.add(apiCall);
            }
        });
        content = content
                .replace("//TODO entityLists", String.join("\n  ", entityList))
                .replace("//TODO ApiCalls", String.join("\n\n    ", apiCalls));
        return helperService.replaceFileContent(content, classContent.getName());
    }

    private String generateAdminCreateScssComponent() {
        return helperService.getFileContent(helperService.getFilePath("FrontendAdminCreateScssComponent"));
    }

    private String generateAdminDetailHtmlComponent(ClassContent classContent) {
        String content = helperService.getFileContent(helperService.getFilePath("FrontendAdminDetailHtmlComponent"));
        List<String> itemList = new ArrayList<>();
        List<String> listList = new ArrayList<>();
        classContent.getValues().forEach(value -> {
            if (!value.getType().startsWith("List<")) {
                String item = "<div class=\"info-item\">\n" +
                        "          <span class=\"label\">ReplacementColumn:</span>\n" +
                        "          <span class=\"value\">{{ replacement.replacementColumntypeRep }}</span>\n" +
                        "        </div>";
                itemList.add(item
                        .replace("typeRep", getTypeDisplay(value.getType()))
                        .replace("replacementColumn", value.getName())
                        .replace("ReplacementColumn", helperService.capitalizeFirstLetter(value.getName())));
            } else {
                String list = "<div class=\"list-container\">\n" +
                        "            <h2>ReplacementColumn</h2>\n" +
                        "            <div class=\"list\">\n" +
                        "                <div class=\"list-item\" *ngFor=\"let item of replacement.replacementColumn\">\n" +
                        "                    <span class=\"id\">{{ item.id }}</span>\n" +
                        "                    <span class=\"value\">{{ item.name }}</span>\n" +
                        "                </div>\n" +
                        "            </div>\n" +
                        "        </div>";
                listList.add(list
                        .replace("replacementColumn", value.getName())
                        .replace("ReplacementColumn", helperService.capitalizeFirstLetter(value.getName())));
            }
        });
        content = content.replace("//TODO ItemList", String.join("\n                ", itemList));
        content = content.replace("//TODO ListList", String.join("\n        ", listList));
        return helperService.replaceFileContent(content, classContent.getName());
    }

    private String generateAdminDetailTSComponent(ClassContent classContent) {
        String content = helperService.getFileContent(helperService.getFilePath("FrontendAdminDetailTsComponent"));
        return helperService.replaceFileContent(content, classContent.getName());
    }

    private String generateAdminDetailScssComponent(ClassContent classContent) {
        return helperService.getFileContent(helperService.getFilePath("FrontendAdminDetailScssComponent"));
    }

    private String getInputFieldType(String type) {
        return switch (type) {
            case "int", "Integer", "double" -> "number";
            case "Date", "Instant" -> "date";
            default -> "text";
        };
    }

    public List<FileDto> getTSFile(List<MultipartFile> files) {
        List<FileDto> tsFiles = new ArrayList<>();
        files.forEach(file -> {
            ClassContent classInformation = helperService.getClassFromFile(helperService.getFileContent(file));
            tsFiles.add(new FileDto(Objects.requireNonNull(file.getOriginalFilename()).replace("java", "ts"),
                    getTSFileFromContent(classInformation)));
        });
        return tsFiles;
    }

    private static String getTSFileFromContent(ClassContent content) {
        StringBuilder tsContent = new StringBuilder("export interface " + content.getName() + " {\n");
        content.getValues().forEach(v -> {
            tsContent.append("  ").append(v.getName()).append(": ").append(convertJavaTypeToTSType(v.getType())).append("\n");
        });
        tsContent.append("}");
        return tsContent.toString();
    }

    private static String convertJavaTypeToTSType(String s) {
        switch (s) {
            case "String":
                return "string";
            case "int", "Integer", "double":
                return "number";
            case "boolean", "Boolean":
                return "boolean";
            case "Instant":
                return "Date";
            default: {
                if (s.contains("List<")) {
                    String sNoList = s.substring(0, s.length() - 1).replace("List<", "");
                    return sNoList + "[]";
                } else {
                    return s;
                }
            }
        }
    }

    public List<FileDto> generateService(List<MultipartFile> files) {
        List<FileDto> contentList = new ArrayList<>();
        files.forEach(file -> {
            ControllerContent controllerContent = generateServiceFile(file);
            String content = "  // " + controllerContent.getName() + "\n\n";
            List<String> methods = new ArrayList<>();
            controllerContent.getRequestsList().forEach(request -> {
                String firstLine = "  " + request.getMethodName() + "(" + getRequestParamsContent(request) + "): Observable<" + request.getReturnType() + "> {";
                String body = ", " +  request.getRequestParams().stream().filter(param -> param.getType() == ControllerContent.RequestParamType.REQUEST_BODY).findFirst()
                        .map(ControllerContent.RequestParam::getValue).orElse("");
                String secondLine = "return this." + request.getMethod().toLowerCase()
                        + "<" + request.getReturnType() + ">(`" +
                        controllerContent.getBasePath() + request.getPath() + "`" + body + ")";
                methods.add(firstLine + "\n    " + secondLine + "\n  }");
            });
            content += String.join("\n\n", methods);
            contentList.add(new FileDto(controllerContent.getName(), content));
        });
        return contentList;
    }

    private String getRequestParamsContent(ControllerContent.RequestMapping requestMapping) {
        List<String> contentList = new ArrayList<>();
        requestMapping.getRequestParams().forEach(requestParam -> contentList.add(requestParam.getValue() + ": " + requestParam.getClassName()));
        return String.join(", ", contentList);
    }

    private ControllerContent generateServiceFile(MultipartFile file) {
        ControllerContent controllerContent = new ControllerContent();
        String content = helperService.getFileContent(file);
        List<String> lines = Arrays.stream(content.split("\n")).toList();
        List<ControllerContent.RequestMapping> requestMappings = new ArrayList<>();
        for (int i = 0; i < lines.toArray().length; i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("@RequestMapping")) {
                controllerContent.setBasePath(getPath(line));
            }
            if (line.startsWith("public class")) {
                controllerContent.setName(line.split(" ")[2]);
            }
            if (line.startsWith("@PostMapping") || line.startsWith("@GetMapping") || line.startsWith("@PutMapping") || line.startsWith("@DeleteMapping")) {
                ControllerContent.RequestMapping requestMapping = new ControllerContent.RequestMapping();
                if (line.trim().startsWith("@PostMapping")) {
                    requestMapping.setMethod("POST");
                } else if (line.trim().startsWith("@GetMapping")) {
                    requestMapping.setMethod("GET");
                } else if (line.trim().startsWith("@PutMapping")) {
                    requestMapping.setMethod("PUT");
                } else if (line.trim().startsWith("@DeleteMapping")) {
                    requestMapping.setMethod("DELETE");
                }
                String lineAfter = lines.get(i + 1).trim();
                if (line.contains("(")) {
                    requestMapping.setPath(getPath(line));
                }
                if (lineAfter.contains("public")) {
                    String[] words = lineAfter.split(" ");
                    String tsType = words[1];
                    if (tsType.startsWith("ResponseEntity<")) {
                        tsType = tsType.replace("ResponseEntity<", "");
                        tsType = tsType.substring(0, tsType.length() - 1);
                    }
                    requestMapping.setReturnType(convertJavaTypeToTSType(tsType));
                    int pos = lineAfter.indexOf("(");
                    String beforeParen = lineAfter.substring(0, pos);

                    String[] methodWords = beforeParen.split(" ");
                    requestMapping.setMethodName(methodWords[methodWords.length - 1]);
                    List<String> methodParams = Arrays.stream(lineAfter.substring(lineAfter.indexOf("(") + 1, lineAfter.indexOf(")")).split(",")).toList();
                    List<ControllerContent.RequestParam> params = new ArrayList<>();
                    methodParams.forEach(param -> {
                        if (!param.isEmpty()) {
                            ControllerContent.RequestParamType type = ControllerContent.RequestParamType.REQUEST_PARAM;
                            if (param.contains("@PathVariable")) {
                                type = ControllerContent.RequestParamType.PATH_VARIABLE;
                            } else if (param.contains("@RequestBody")) {
                                type = ControllerContent.RequestParamType.REQUEST_BODY;
                            }
                            String[] paramWords = param.split(" ");
                            params.add(new ControllerContent.RequestParam(type, convertJavaTypeToTSType(paramWords[1]), paramWords[2]));
                        }
                    });
                    requestMapping.setRequestParams(params);
                }
                if (requestMapping.getPath() == null) requestMapping.setPath("");
                requestMappings.add(requestMapping);
            }
        }
        controllerContent.setRequestsList(requestMappings);
        return controllerContent;
    }

    private String getPath(String line) {
        String path = line.substring(line.indexOf("(") + 2, line.indexOf("\")"));
        return path.replace("{", "${");
    }
}
