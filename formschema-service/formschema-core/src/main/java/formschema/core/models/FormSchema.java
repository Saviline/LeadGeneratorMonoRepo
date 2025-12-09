package formschema.core.models;

import java.util.List;

import lombok.Data;

@Data
public class FormSchema {
    
    private String id; // The unique SchemaID
    
    private String name; // e.g., "B2B Webinar Signup"
    private String version; // e.g., "1.0"
    
    // Here is the list of questions/inputs
    private List<FormField> fields;
}