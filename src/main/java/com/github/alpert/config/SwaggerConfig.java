package com.github.alpert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.alpert.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    // This is not working for a reason?
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("kisalt REST API")
                .description("\"kisalt url shortener api\"")
                .version("0.0.1")
                .license("GNU Lesser General Public License V3")
                .licenseUrl("https://www.gnu.org/licenses/lgpl-3.0.en.html\"")
                .contact(new Contact("Alper Tekinalp", "https://alpert.github.io/about/", "alper.tekinalp@gmail.com"))
                .build();
    }
}