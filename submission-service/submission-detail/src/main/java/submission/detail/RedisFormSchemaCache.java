package submission.detail;

import submission.core.ports.ICacheFormSchema;

public class RedisFormSchemaCache implements ICacheFormSchema {

    @Override
    public void save(String formSchemaId, String validationSchema) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public String getById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public Boolean exists(String schemaId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exists'");
    }

}
