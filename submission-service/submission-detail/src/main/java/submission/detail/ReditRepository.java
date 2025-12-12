package submission.detail;

import java.util.concurrent.ConcurrentHashMap;


public class ReditRepository {

    final ConcurrentHashMap<String, String> SCHEMA_STORE = new ConcurrentHashMap<>();

    public void saveFormSchema(String name, String schemaJson) {
        SCHEMA_STORE.put(name, schemaJson);
    }

    public String getFormSchemaByName(String name) {
        return SCHEMA_STORE.get(name);
    }
}
