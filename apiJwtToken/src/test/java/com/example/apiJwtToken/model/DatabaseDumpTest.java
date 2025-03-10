package com.example.apiJwtToken.model;

    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
    import org.springframework.test.context.TestPropertySource;
    import org.springframework.test.context.jdbc.Sql;
    import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

    import java.util.List;
    import java.util.Map;

    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(locations = "classpath:application-test.properties")
    @Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
    public class DatabaseDumpTest {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @MockBean
        private AuthenticationProvider authenticationProvider;

        @MockBean
        private SecurityContextLogoutHandler securityContextLogoutHandler;

        @Test
        void dumpTables() {
            dumpTable("app_users");
            dumpTable("applications");
            dumpTable("roles");
            // Add more tables as needed
        }

        private void dumpTable(String tableName) {
            System.out.println("--- Dumping table: " + tableName + " ---");
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + tableName);
            for (Map<String, Object> row : rows) {
                System.out.println(row);
            }
            System.out.println("--- End of table: " + tableName + " ---");
        }
    }