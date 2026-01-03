package submission.core.ports;

public interface ICacheFormSchema {
    public void save(String formSchemaId, String validationSchema);
    public String getById(String id);
}
