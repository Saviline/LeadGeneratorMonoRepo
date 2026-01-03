package formschema.core.domain;

import lombok.Data;

@Data
public class FormField {
    private String key;
    private String label;
    private String type;
    private String mapTo;

    private ValidationRules validation;
}