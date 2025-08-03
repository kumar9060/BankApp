package com.security.bank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "bootstrap.admin")
public class BootstrapAdminProperties {

    private String username;
    private String password;
}
