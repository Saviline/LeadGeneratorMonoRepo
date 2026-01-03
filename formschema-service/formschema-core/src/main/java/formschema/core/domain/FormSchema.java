package formschema.core.domain;

import java.util.List;

import lombok.Data;

@Data
public class FormSchema {

    private String id;
    private String customerId;

    private String name;
    private String version;

    private List<FormField> fields;
}