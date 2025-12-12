package submission.detail;

import java.util.List;

import submission.detail.dto.FormSchema;

public class ReditRepository {

    List<FormSchema> formSchemas = new java.util.ArrayList<>();

    public void saveFormSchema(FormSchema schema) {
        formSchemas.add(schema);
    }

    public FormSchema getFormSchemaByName(String name) {
        return formSchemas.stream()
                .filter(schema -> schema.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
