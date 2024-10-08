package ke.co.apollo.health.common.config;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

public class SwaggerCommonConfig {

  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {
    ApiInfoBuilder builder = new ApiInfoBuilder();
    return builder
        .title("APOLLO GROUP")
        .description("APA Health Integration APIs")
        .version("1.0")
        .contact(this.getContact()).build();
  }

  private Contact getContact() {
    return new Contact("APA Health", "https://www.apahealth.co.ke", "apa.digital@apollo.co.ke");
  }

}
