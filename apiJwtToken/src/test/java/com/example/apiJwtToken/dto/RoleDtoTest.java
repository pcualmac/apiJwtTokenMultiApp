package com.example.apiJwtToken.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleDtoTest {

    @Test
    void testEqualsAndHashCode() {
        RoleDto roleDto1 = new RoleDto(1L, "ROLE_ADMIN");
        RoleDto roleDto2 = new RoleDto(1L, "ROLE_ADMIN");
        RoleDto roleDto3 = new RoleDto(2L, "ROLE_USER");
        RoleDto roleDto4 = new RoleDto(1L, "ROLE_USER");

        assertEquals(roleDto1, roleDto2);
        assertEquals(roleDto1.hashCode(), roleDto2.hashCode());

        assertNotEquals(roleDto1, roleDto3);
        assertNotEquals(roleDto1.hashCode(), roleDto3.hashCode());

        assertNotEquals(roleDto1, roleDto4);
        assertNotEquals(roleDto1.hashCode(), roleDto4.hashCode());

        assertNotEquals(roleDto3, roleDto4);
        assertNotEquals(roleDto3.hashCode(), roleDto4.hashCode());
    }

    @Test
    void testToString() {
        RoleDto roleDto = new RoleDto(1L, "ROLE_ADMIN");
        String expectedToString = "RoleDto{id=1, roleName='ROLE_ADMIN'}";
        assertEquals(expectedToString, roleDto.toString());
    }

    @Test
    void testGettersAndSetters() {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(1L);
        roleDto.setRoleName("ROLE_ADMIN");

        assertEquals(1L, roleDto.getId());
        assertEquals("ROLE_ADMIN", roleDto.getRoleName());
    }

    @Test
    void testConstructor() {
        RoleDto roleDto = new RoleDto(1L, "ROLE_ADMIN");

        assertEquals(1L, roleDto.getId());
        assertEquals("ROLE_ADMIN", roleDto.getRoleName());
    }

    @Test
    void testEqualsWithNull() {
        RoleDto roleDto = new RoleDto(1L, "ROLE_ADMIN");
        assertNotEquals(roleDto, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        RoleDto roleDto = new RoleDto(1L, "ROLE_ADMIN");
        assertNotEquals(roleDto, "String");
    }

    @Test
    void testEqualsWithNullFields() {
        RoleDto roleDto1 = new RoleDto(null, null);
        RoleDto roleDto2 = new RoleDto(null, null);
        RoleDto roleDto3 = new RoleDto(1L, null);
        RoleDto roleDto4 = new RoleDto(null, "ROLE_ADMIN");

        assertEquals(roleDto1, roleDto2);
        assertEquals(roleDto1.hashCode(), roleDto2.hashCode());

        assertNotEquals(roleDto1, roleDto3);
        assertNotEquals(roleDto1.hashCode(), roleDto3.hashCode());

        assertNotEquals(roleDto1, roleDto4);
        assertNotEquals(roleDto1.hashCode(), roleDto4.hashCode());
    }
}