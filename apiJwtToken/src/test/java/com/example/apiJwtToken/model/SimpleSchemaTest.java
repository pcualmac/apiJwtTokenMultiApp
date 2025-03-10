package com.example.apiJwtToken.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest
@Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SimpleSchemaTest {

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private SecurityContextLogoutHandler securityContextLogoutHandler;

    @Test
    void testSchemaLoading() {
        // Empty test
    }
}