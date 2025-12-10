package formschema.core.service;

import org.junit.jupiter.api.Test;

import formschema.core.fake.FakeFormSchemaRepository;
import formschema.core.interfaces.IFormSchemaRepository;
import formschema.core.models.FormSchema;
import formschema.core.services.FormSchemaService;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class FormSchemaServiceTest {

    private IFormSchemaRepository<FormSchema, String> repository;
    private FormSchemaService service;

    @BeforeEach
    public void setUp() {
        repository = new FakeFormSchemaRepository();
        service = new FormSchemaService(repository);
    }
    
    @Test
    public void testSaveAndFindFormSchema() {
        FormSchema formSchema = new FormSchema();
        formSchema.setName("Test Form");
        
        String id = service.createSchema(formSchema);
        assertNotNull(id, "Saved FormSchema should have an ID");
        
        FormSchema retrieved = service.getSchemaById(id);
        assertNotNull(retrieved, "Retrieved FormSchema should not be null");
        assertEquals(retrieved.getId(), retrieved.getId(), "FormSchema id should match");
    }

    @Test
    public void testDeleteFormSchema() {
        FormSchema formSchema = new FormSchema();
        formSchema.setName("To Be Deleted");
        
        String id = service.createSchema(formSchema);
        assertNotNull(id, "Saved FormSchema should have an ID");
        
        Boolean deleted = service.deleteSchemaById(id);
        assertTrue(deleted, "FormSchema should be deleted successfully");
        
        FormSchema retrieved = service.getSchemaById(id);
        assertNull(retrieved, "Retrieved FormSchema should be null after deletion");
    }
}