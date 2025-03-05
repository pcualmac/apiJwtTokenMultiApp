package com.example.apiJwtToken.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component  // This is required
@ConfigurationProperties(prefix = "spring")
public class AppSpringProperties {
    @Value("${spring.application.name}")
    private String name;
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    @Value("${spring.security.user.name}")
    private String securityUserName;
    @Value("${spring.security.user.password}")
    private String securityUserPassword;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String applicationName) { this.name = applicationName; }

    public String getDatasourceUsername() { return datasourceUsername; }
    public void setDatasourceUsername(String datasourceUsername) { this.datasourceUsername = datasourceUsername; }

    public String getDatasourcePassword() { return datasourcePassword; }
    public void setDatasourcePassword(String datasourcePassword) { this.datasourcePassword = datasourcePassword; }

    public String getSecurityUserName() { return securityUserName; }
    public void setSecurityUserName(String securityUserName) { this.securityUserName = securityUserName; }

    public String getSecurityUserPassword() { return securityUserPassword; }
    public void setSecurityUserPassword(String securityUserPassword) { this.securityUserPassword = securityUserPassword; }
}
