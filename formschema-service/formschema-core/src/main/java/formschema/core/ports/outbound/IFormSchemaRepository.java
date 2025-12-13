package formschema.core.ports.outbound;

public interface IFormSchemaRepository<T, ID> {

    ID save(T entity);

    T findFormSchemaById(ID id);

    Boolean deleteFormSchema(ID id);
    
}

