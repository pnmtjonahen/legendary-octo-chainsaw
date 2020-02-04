package nl.tjonahen.resto;

import brave.sampler.Sampler;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableSwagger2
public class ChefApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChefApplication.class, args);
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

    /*
    * Swagger 2 API documentation
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("nl.tjonahen.resto"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfo(
                        "Kitchen REST API",
                        "Kitchen REST API used by the diner.",
                        "V1",
                        "n/a",
                        new Contact("PN Tjon-A-Hen", "www.tjonahen.nl", "philippe@tjonahen.nl"),
                        "DBAD", 
                        "https://dbad-license.org/", 
                        Collections.<VendorExtension>emptyList()));
    }
}
