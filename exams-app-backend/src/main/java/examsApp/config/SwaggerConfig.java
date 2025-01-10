package examsApp.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.*;


@Configuration
@OpenAPIDefinition(
  info =@Info(
    title = "Exams API",
    version = "Version 1.0",
    contact = @Contact(
      name = "Angel Benitez", email = "benitezgomezan@gmail.com", url = "https://github.com/angeben"
    ),
    license = @License(
      name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
    ),
    //termsOfService = "",
    description = "Spring Boot RestFul API - App for the management of exams"
  )
)
public class SwaggerConfig {
}
