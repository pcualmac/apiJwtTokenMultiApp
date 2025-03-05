// package com.example.apiJwtToken.config;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.TestPropertySource;

// import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest
// @EnableConfigurationProperties(AppSpringProperties.class)
// @ActiveProfiles("live")
// class AppSpringPropertiesTest {

//     @Autowired
//     private AppSpringProperties appSpringProperties;

//     @Test
//     void debugSpringProperties() {
//         assertThat(appSpringProperties.getName()).isEqualTo("apiJwtToken");
//         assertThat(appSpringProperties.getDatasourceUsername()).isEqualTo("sa");
//         assertThat(appSpringProperties.getDatasourcePassword()).isEqualTo("");
//         assertThat(appSpringProperties.getSecurityUserName()).isEqualTo("your_username");
//         assertThat(appSpringProperties.getSecurityUserPassword()).isEqualTo("your_password");
//     }
    
// }


package com.example.apiJwtToken.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AppSpringProperties.class) // Load only AppSpringProperties
@ActiveProfiles("live")
public class AppSpringPropertiesTest {

    @Autowired
    private AppSpringProperties appSpringProperties;

    @Test
    void debugSpringProperties() {
        assertThat(appSpringProperties.getName()).isEqualTo("apiJwtToken");
        assertThat(appSpringProperties.getDatasourceUsername()).isEqualTo("root");
        assertThat(appSpringProperties.getDatasourcePassword()).isEqualTo("rootpassword");
        assertThat(appSpringProperties.getSecurityUserName()).isEqualTo("your_username");
        assertThat(appSpringProperties.getSecurityUserPassword()).isEqualTo("your_password");
    }
}