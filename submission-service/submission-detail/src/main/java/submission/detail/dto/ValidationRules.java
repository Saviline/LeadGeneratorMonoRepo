package submission.detail.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationRules {
    private boolean required;
    private String regex;
    private Integer min;
    private Integer max;
    private String errorMessage;
}