package submission.detail.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldDefinition {
    private String key;
    private String label;
    private String type;
    private ValidationRules validation;
}