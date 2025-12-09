package formschema.core.models;

import lombok.Data;

@Data
public class FormField {
    private String key;     // "txt_email"
    private String label;   // "Enter Work Email"
    private String type;    // "email", "text", "number"
    private String mapTo;   // "profile.email" (The logic link)

    private ValidationRules validation;
    private BusinessRules businessRules;
}