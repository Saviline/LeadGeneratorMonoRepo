package fake;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import submission.core.ports.ICacheFormSchema;

public class FakeFormSchemaCache implements ICacheFormSchema{

    ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public void save(String formSchemaId, String validationSchema) {
        cache.put(formSchemaId, validationSchema);
    }

    @Override
    public String getById(String id) {
        return cache.get(id);
    }

    @Override
    public Boolean exists(String schemaId) {
        return cache.containsKey(schemaId);
    }
    
}
