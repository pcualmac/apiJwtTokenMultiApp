package com.example.apiJwtToken.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EndpointController {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @GetMapping("/endpoints")
    public List<String> getAllEndpoints() {
        return requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
            .map(entry -> {
                RequestMappingInfo mapping = entry.getKey();
                String patterns = mapping.getPathPatternsCondition() != null
                        ? mapping.getPathPatternsCondition().getPatterns().toString()
                        : (mapping.getPatternsCondition() != null
                        ? mapping.getPatternsCondition().getPatterns().toString()
                        : "Unknown pattern");
                
                String methods = mapping.getMethodsCondition().getMethods().toString();

                return methods + " -> " + patterns;
            })
            .collect(Collectors.toList());
    }
}
