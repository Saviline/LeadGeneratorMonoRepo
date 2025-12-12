package submission.detail;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

public class ValidatorService {

    private final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    private final ObjectMapper mapper = new ObjectMapper();

    // Look! No "FormSchema" object here. Just a JSON String.
    public void validate(Map<String, Object> submissionPayload, String standardSchemaJson) {
        
        try {
            // 1. Parse the Standard Rules
            JsonSchema schema = factory.getSchema(standardSchemaJson);
            
            // 2. Convert Map -> JsonNode
            JsonNode jsonNode = mapper.valueToTree(submissionPayload);
            
            // 3. Validate
            Set<ValidationMessage> errors = schema.validate(jsonNode);
            
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Validation Failed: " + errors);
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}