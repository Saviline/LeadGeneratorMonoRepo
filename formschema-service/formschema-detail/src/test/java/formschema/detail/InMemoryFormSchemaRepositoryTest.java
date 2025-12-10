package formschema.detail;
import formschema.core.models.FormSchema;
import formschema.detail.inmemory.InMemoryFormSchemaRepository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

class InMemoryFormSchemaRepositoryTest {
    
    private InMemoryFormSchemaRepository repository;

    @BeforeEach 
    void setUp() 
    {
        repository = new InMemoryFormSchemaRepository();
    }
    
    @Test
    void should_save_schema() 
    {
        //Arrange
        FormSchema schema = new FormSchema();
        schema.setName("Test Schema");

        //Act
        String id = repository.save(schema);

        //Assert
        FormSchema found = repository.findFormSchemaById(id);

        assertEquals(found.getId(), id);
    }
}
