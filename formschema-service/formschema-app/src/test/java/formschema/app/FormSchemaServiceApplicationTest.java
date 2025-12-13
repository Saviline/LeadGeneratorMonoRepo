package formschema.app;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import formschema.core.domain.FormSchema;

@Testcontainers
public class FormSchemaServiceApplicationTest extends BaseIntegration 
{
    @Test
    void PostSchema_ShouldReturn_ID() 
    {
        //Arrange
        FormSchema schema = new FormSchema();
        schema.setName("Lead Gen");
        schema.setVersion("1.0");

        //Act
        ResponseEntity<String> id = restTemplate.postForEntity(
            "http://localhost:" + serverPort + "/api/schemas", 
            schema, 
            String.class
        );

        System.out.println("This is the id: " + id.getBody());

        //Assert
        assert(id.getStatusCode().is2xxSuccessful());
    }    

    @Test
    void GetSchema_ShouldReturn_Schema() 
    {
        //Arrange
        FormSchema schema = new FormSchema();
        schema.setName("Lead Gen");
        schema.setVersion("1.0");

        ResponseEntity<String> idResponse = restTemplate.postForEntity(
            "http://localhost:" + serverPort + "/api/schemas", 
            schema, 
            String.class
        );

        String id = idResponse.getBody();

        //Act
        ResponseEntity<FormSchema> getResponse = restTemplate.getForEntity(
            "http://localhost:" + serverPort + "/api/schemas/" + id, 
            FormSchema.class
        );

        FormSchema fetchedSchema = getResponse.getBody();

        //Assert
        assert(getResponse.getStatusCode().is2xxSuccessful());
        assert(fetchedSchema != null);
        assert(fetchedSchema.getName().equals("Lead Gen"));
        assert(fetchedSchema.getVersion().equals("1.0"));
    }
}
