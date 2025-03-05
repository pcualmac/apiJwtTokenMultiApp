package com.example.apiJwtToken.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class SecurityConfigFilterOrderTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegisterEndpointNotPermit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    //@Test
    void testRegisterEndpointPermitted() throws Exception {
        String uniqueUsername = "testuser-" + UUID.randomUUID().toString(); // Generate unique username
        String requestBody = "{\n" +
                "  \"username\": \"" + uniqueUsername + "\",\n" +
                "  \"password\": \"testpassword\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk()); // Or isCreated(), depending on your controller
    }
}