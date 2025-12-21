package formschema.core.application;

public class SchemaNotFoundException extends RuntimeException {
    public SchemaNotFoundException(String schemaId) {
        super("Schema not found: " + schemaId);
    }
}
