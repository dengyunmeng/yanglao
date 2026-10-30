package com.elderlycare.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Integer expireHours;
    private String header = "Authorization";
    private String prefix = "Bearer ";
}
