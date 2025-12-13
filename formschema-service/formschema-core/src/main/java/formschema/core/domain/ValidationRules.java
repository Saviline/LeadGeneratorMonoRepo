package formschema.core.domain;

import lombok.Data;

@Data
public class ValidationRules {
    private boolean required;
    private String regex;       // "^[A-Z0-9]+$"
    private Integer min;        // For numbers
    private Integer max;
    private String errorMessage;
}

