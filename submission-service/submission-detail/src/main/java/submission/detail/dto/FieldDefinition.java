package submission.detail.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldDefinition {
    private String key;         // e.g., "email_address"
    private String label;       // e.g., "Your Email"
    private String type;        // e.g., "email", "number"
    private ValidationRules validation;
}