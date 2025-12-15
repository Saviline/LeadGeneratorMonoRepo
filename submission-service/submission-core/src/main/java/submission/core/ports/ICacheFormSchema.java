package submission.core.ports;

public interface ICacheFormSchema {
    public String save(String formSchemaId, String validationSchema);
    public String getById(String id);
    Boolean exists(String schemaId);
}
