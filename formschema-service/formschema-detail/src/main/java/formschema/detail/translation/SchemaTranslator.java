package formschema.detail.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import formschema.core.domain.FormField;
import formschema.core.domain.FormSchema;

public class SchemaTranslator {

    // Output: Standard Draft-7 JSON String
    public Map<String, Object> toValidationPayload(FormSchema schema) {
        Map<String, Object> jsonSchema = new HashMap<>();
        jsonSchema.put("$schema", "http://json-schema.org/draft-07/schema#");
        jsonSchema.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        for (FormField field : schema.getFields()) {
            Map<String, Object> rule = new HashMap<>();
            
            // 1. Generic Type Mapping
            String type = field.getType().toLowerCase();
            switch (type) {
                case "number": rule.put("type", "number"); break;
                case "email":  rule.put("type", "string"); rule.put("format", "email"); break;
                default:       rule.put("type", "string");
            }

            // 2. Validation Rules
            if (field.getValidation() != null) {
                if (field.getValidation().isRequired()) required.add(field.getKey());
                if (field.getValidation().getMin() != null) rule.put("minimum", field.getValidation().getMin());
                if (field.getValidation().getRegex() != null) rule.put("pattern", field.getValidation().getRegex());
            }
            properties.put(field.getKey(), rule);
        }
        
        jsonSchema.put("properties", properties);
        jsonSchema.put("required", required);

        // WRAP IT in an event payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("schemaId", schema.getId());
        payload.put("validationSchema", jsonSchema); // <--- The Standard format
        
        return payload;
    }
}