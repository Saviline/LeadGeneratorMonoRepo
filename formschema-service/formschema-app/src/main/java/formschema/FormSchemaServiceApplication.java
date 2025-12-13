
package formschema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("formschema.app.config")
public class FormSchemaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FormSchemaServiceApplication.class, args);
    }
}
