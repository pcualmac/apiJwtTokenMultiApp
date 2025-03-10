package com.example.apiJwtToken.config;

    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

    import static org.junit.jupiter.api.Assertions.assertInstanceOf;
    import static org.junit.jupiter.api.Assertions.assertNotNull;

    @SpringBootTest
    public class PasswordEncoderConfigTest {

        @Autowired
        private PasswordEncoder passwordEncoder;

        @MockBean
        private SecurityContextLogoutHandler securityContextLogoutHandler;

        @Test
        public void testPasswordEncoderBeanCreation() {
            assertNotNull(passwordEncoder);
            assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
        }

        @Test
        public void testPasswordEncoderBeanEncoding() {
            String rawPassword = "testPassword";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            assertNotNull(encodedPassword);
            // We can't directly compare encoded passwords as bcrypt generates different hashes each time.
            // We can however check that it encodes without exception.
        }
    }