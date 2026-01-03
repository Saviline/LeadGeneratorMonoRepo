package submission.detail.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormSchema {
    private String name;
    private String version;
    private List<FieldDefinition> fields;
}

