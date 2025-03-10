package com.example.apiJwtToken.config;

    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.http.MediaType;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
    import org.springframework.test.context.TestPropertySource;
    import org.springframework.test.context.jdbc.Sql;
    import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
    import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(locations = "classpath:application-test.properties")
    @Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public class SecurityConfigFilterOrderTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthenticationProvider authenticationProvider;

        @MockBean
        private SecurityContextLogoutHandler securityContextLogoutHandler;

        @Test
        void testRegisterEndpointNotPermit() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register"))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testRegisterEndpointPermitted() throws Exception {
            String uniqueUsername = "testuser-".toString();
            String uniqueEmail = uniqueUsername + "@example.com";
            String requestBody = "{\n" +
                    "  \"username\": \"" + uniqueUsername + "\",\n" +
                    "  \"password\": \"testpassword1\",\n" +
                    "  \"email\": \"" + uniqueEmail + "\"\n" +
                    "}";

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        void testRegisterEndpointApp1Permitted() throws Exception {
            String uniqueUsername = "jerry-".toString();
            String uniqueEmail = uniqueUsername + "2@example.com";
            String requestBody = "{\n" +
                    "  \"username\": \"" + uniqueUsername + "\",\n" +
                    "  \"password\": \"testpassword\",\n" +
                    "  \"email\": \"" + uniqueEmail + "\"\n" +
                    "}";

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/TestApp1/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }