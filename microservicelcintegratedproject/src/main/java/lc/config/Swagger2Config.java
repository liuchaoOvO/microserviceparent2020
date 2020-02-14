package lc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author liuchaoOvO on 2019/8/16
 *
 * http://localhost:12580/swagger-ui.html
 */


@Configuration
public class Swagger2Config {
    @Bean
    public Docket config(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("lc"))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("LC集成的API文档")
                .description("这是一段description描述：供测试使用的API")
                .contact(new Contact("LC","https://www.baidu.com","liuchaoy@yonyou.com"))
                .version("1.0")
                .build();
    }
}
