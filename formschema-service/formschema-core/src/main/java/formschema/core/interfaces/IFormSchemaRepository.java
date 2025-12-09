package formschema.core.interfaces;

public interface IFormSchemaRepository<T, ID> {

    ID save(T entity);

    T findFormSchemaById(ID id);

    Boolean deleteFormSchema(ID id);
    
}

