package org.homomorphicpayroll.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPI30Configuration {

    @Bean
    public OpenAPI customizeOpenAPI() {

        return new OpenAPI()
                .info(new Info().title("Homomorphic-Payroll-Processing")
                        .description("This is a sample Homomorphic Payroll Processing System")
                        .version("1.0.0"));
    }
}
