package formschema.core.domain;

import lombok.Data;

@Data
public class ValidationRules {
    private boolean required;
    private String regex;
    private Integer min;
    private Integer max;
    private String errorMessage;
}

