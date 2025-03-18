package com.example.apiJwtToken.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ApplicationDto {

    private Long id;
    private String applicationName;
    private LocalDateTime createdAt;
    private List<Long> userIds;

    // Constructors, getters, setters
    public ApplicationDto() {
    }

    public ApplicationDto(Long id, String applicationName, LocalDateTime createdAt) {
        this.id = id;
        this.applicationName = applicationName;
        this.createdAt = createdAt;
    }

    public ApplicationDto(Long id, String applicationName, List<Long> userIds) {
        this.id = id;
        this.applicationName = applicationName;
        this.userIds = userIds;
    }

    public ApplicationDto(Long id, String applicationName, LocalDateTime createdAt, List<Long> userIds) {
        this.id = id;
        this.applicationName = applicationName;
        this.createdAt = createdAt;
        this.userIds = userIds;
    }

    //getters and setters.
    public Long getId() {
        return this.id;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Long> getUserIds() {
        return this.userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationDto that = (ApplicationDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(applicationName, that.applicationName) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(userIds, that.userIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, applicationName, createdAt, userIds);
    }

    @Override
    public String toString() {
        return "ApplicationDto{" +
                "id=" + id +
                ", applicationName='" + applicationName + '\'' +
                ", createdAt=" + createdAt +
                ", userIds=" + userIds +
                '}';
    }
}