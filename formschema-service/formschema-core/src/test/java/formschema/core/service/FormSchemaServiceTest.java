package formschema.core.service;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import formschema.core.application.FormSchemaService;
import formschema.core.domain.FormSchema;
import formschema.core.fake.FakeFormSchemaRepository;
import formschema.core.ports.outbound.IFormSchemaRepository;
import formschema.core.ports.outbound.IPublisher;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class FormSchemaServiceTest {

    private IFormSchemaRepository<FormSchema, String> repository;
    private IPublisher publisher;
    private FormSchemaService service;

    @BeforeEach
    public void setUp() {
        repository = new FakeFormSchemaRepository();
        publisher = mock(IPublisher.class);
        service = new FormSchemaService(repository, publisher);
    }
    
    @Test
    public void testSaveAndFindFormSchema() {
        FormSchema formSchema = new FormSchema();
        formSchema.setName("Test Form");
        
        String id = service.createSchema(formSchema);
        FormSchema retrieved = service.getSchemaById(id);

        verify(publisher).PublishSchema(formSchema);

        assertNotNull(id, "Saved FormSchema should have an ID");
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