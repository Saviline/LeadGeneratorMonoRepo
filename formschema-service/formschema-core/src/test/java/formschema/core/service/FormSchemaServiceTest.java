package formschema.core.service;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import formschema.core.application.FormSchemaService;
import formschema.core.application.SchemaNotFoundException;
import formschema.core.domain.FormSchema;
import formschema.core.fake.FakeFormSchemaRepository;
import formschema.core.ports.outbound.IFormSchemaRepository;
import formschema.core.ports.outbound.IPublisher;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class FormSchemaServiceTest {

    private static final String CUSTOMER_ID = "test-customer-123";

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
    void createSchema_ShouldAssignUUIDAndCustomerId() {
        FormSchema formSchema = new FormSchema();
        formSchema.setName("Test Form");

        String id = service.createSchema(formSchema, CUSTOMER_ID);

        assertNotNull(id, "Schema should have an ID assigned");
        assertEquals(CUSTOMER_ID, formSchema.getCustomerId(), "CustomerId should be set on schema");
        assertEquals(id, formSchema.getId(), "Schema ID should match returned ID");
    }

    @Test
    void createSchema_ShouldPublishSchema() {
        FormSchema formSchema = new FormSchema();
        formSchema.setName("Test Form");

        service.createSchema(formSchema, CUSTOMER_ID);

        verify(publisher).PublishSchema(formSchema);
    }

    @Test
    void getSchemaById_ExistingSchema_ShouldReturnSchema() {
        FormSchema formSchema = new FormSchema();
        formSchema.setName("Test Form");

        String id = service.createSchema(formSchema, CUSTOMER_ID);
        FormSchema retrieved = service.getSchemaById(id, CUSTOMER_ID);

        assertNotNull(retrieved, "Retrieved FormSchema should not be null");
        assertEquals(id, retrieved.getId(), "FormSchema id should match");
        assertEquals("Test Form", retrieved.getName(), "FormSchema name should match");
    }

    @Test
    void getSchemaById_NonExistent_ShouldThrowSchemaNotFoundException() {
        assertThrows(SchemaNotFoundException.class, () -> {
            service.getSchemaById("non-existent-id", CUSTOMER_ID);
        }, "Should throw SchemaNotFoundException for non-existent schema");
    }

    @Test
    void deleteSchemaById_ExistingSchema_ShouldReturnTrue() {
        FormSchema formSchema = new FormSchema();
        formSchema.setName("To Be Deleted");

        String id = service.createSchema(formSchema, CUSTOMER_ID);

        Boolean deleted = service.deleteSchemaById(id, CUSTOMER_ID);

        assertTrue(deleted, "FormSchema should be deleted successfully");
    }

    @Test
    void deleteSchemaById_AfterDeletion_ShouldThrowOnRetrieve() {
        FormSchema formSchema = new FormSchema();
        formSchema.setName("To Be Deleted");

        String id = service.createSchema(formSchema, CUSTOMER_ID);
        service.deleteSchemaById(id, CUSTOMER_ID);

        assertThrows(SchemaNotFoundException.class, () -> {
            service.getSchemaById(id, CUSTOMER_ID);
        }, "Should throw SchemaNotFoundException after deletion");
    }

    @Test
    void deleteSchemaById_NonExistent_ShouldReturnFalse() {
        Boolean deleted = service.deleteSchemaById("non-existent-id", CUSTOMER_ID);

        assertFalse(deleted, "Deleting non-existent schema should return false");
    }

    @Test
    void getAllSchemaByCustomerId_ShouldReturnAllCustomerSchemas() {
        FormSchema schema1 = new FormSchema();
        schema1.setName("Schema 1");
        FormSchema schema2 = new FormSchema();
        schema2.setName("Schema 2");

        service.createSchema(schema1, CUSTOMER_ID);
        service.createSchema(schema2, CUSTOMER_ID);

        List<FormSchema> schemas = service.getAllSchemaByCustomerId(CUSTOMER_ID);

        assertEquals(2, schemas.size(), "Should return 2 schemas");
    }
}