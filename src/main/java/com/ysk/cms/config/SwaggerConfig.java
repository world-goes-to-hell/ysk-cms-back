package com.ysk.cms.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT 토큰을 입력하세요"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("YSK CMS API")
                        .description("YSK CMS REST API Documentation\n\n" +
                                "## 인증 방법\n" +
                                "1. `/api/auth/login` API로 로그인하여 토큰 발급\n" +
                                "2. 우측 상단 `Authorize` 버튼 클릭\n" +
                                "3. 발급받은 accessToken 입력 (Bearer 접두사 불필요)\n\n" +
                                "## 테스트 계정\n" +
                                "- ID: `admin`\n" +
                                "- PW: `admin123`")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .name("bearerAuth")
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
