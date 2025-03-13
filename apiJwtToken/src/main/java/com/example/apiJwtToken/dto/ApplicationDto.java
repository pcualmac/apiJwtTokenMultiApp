package com.example.apiJwtToken.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicationDto {

    private Long id;
    private String applicationName;
    private LocalDateTime createdAt;
    private List<Long> userIds;
    //add any other required fields.

    // Constructors, getters, setters
    public ApplicationDto(Long id, String applicationName, LocalDateTime createdAt) {
        this.id = id;
        this.applicationName = applicationName;
        this.createdAt = createdAt;
    }

    // Constructors, getters, setters
    public ApplicationDto(Long id, String applicationName, List<Long> userIds) {
        this.id = id;
        this.applicationName = applicationName;
        this.userIds = userIds;
    }

    //getters and setters.
    public Long getId(){
        return this.id;
    }
    public String getApplicationName(){
        return this.applicationName;
    }
    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }
    public void setId(Long id){
        this.id = id;
    }
    public void setApplicationName(String applicationName){
        this.applicationName = applicationName;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }


    public List<Long> getUserIds(){
        return this.userIds;
    }

    public void setUserIds(List<Long> userIds){
        this.userIds = userIds;
    }
}