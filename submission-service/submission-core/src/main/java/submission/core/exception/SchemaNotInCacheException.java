package submission.core.exception;

public class SchemaNotInCacheException extends RuntimeException{
    public SchemaNotInCacheException(String schemaId){
        super("Schema not found in cache schemaId: " + schemaId);
    }
    
}
