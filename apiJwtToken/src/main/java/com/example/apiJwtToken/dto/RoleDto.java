package com.example.apiJwtToken.dto;

public class RoleDto {

    private Long id;
    private String roleName;

    // Constructors
    public RoleDto() {
    }

    public RoleDto(Long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    // Optionally, you can add equals(), hashCode(), and toString() methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleDto roleDto = (RoleDto) o;

        if (id != null ? !id.equals(roleDto.id) : roleDto.id != null) return false;
        return roleName != null ? roleName.equals(roleDto.roleName) : roleDto.roleName == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (roleName != null ? roleName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RoleDto{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}