package formschema.core.services;
import java.util.*;

import formschema.core.models.FieldType;
import formschema.core.models.FormField;
import formschema.core.models.FormSchema;
import formschema.core.models.ValidationRules;

public class SchemaTranslatorService {

    public Map<String, Object> generateSubmissionPayload(FormSchema customSchema) {
        
        // 1. Prepare the Standard JSON Schema Structure
        Map<String, Object> jsonSchema = new HashMap<>();
        jsonSchema.put("$schema", "http://json-schema.org/draft-07/schema#");
        jsonSchema.put("type", "object");
        jsonSchema.put("additionalProperties", true); // Satisfies Cliff (Ops) - Allow tracking tags

        Map<String, Object> properties = new HashMap<>();
        List<String> requiredFields = new ArrayList<>();
        
        // 2. Prepare the Error Map (Grimm's Fix for UX)
        // Key: "fieldName.ruleName" -> Value: "Custom Message"
        Map<String, String> errorMap = new HashMap<>();

        if (customSchema.getFields() != null) {
            for (FormField field : customSchema.getFields()) {
                Map<String, Object> fieldDef = new HashMap<>();
                ValidationRules rules = field.getValidation();

                // A. USE ENUM STRATEGY (Clean Code)
                try {
                    FieldType type = FieldType.valueOf(field.getType().toUpperCase());
                    type.apply(fieldDef, rules);
                } catch (IllegalArgumentException e) {
                    // Fallback for unknown types
                    FieldType.TEXT.apply(fieldDef, rules); 
                }

                // B. COMMON VALIDATION & ERROR MAPPING
                if (rules != null) {
                    if (rules.isRequired()) {
                        requiredFields.add(field.getKey());
                        // Map "required" error
                        errorMap.put(field.getKey() + ".required", 
                                     "Field '" + field.getLabel() + "' is required.");
                    }
                    if (rules.getRegex() != null) {
                        fieldDef.put("pattern", rules.getRegex());
                        // Map "pattern" error
                        errorMap.put(field.getKey() + ".pattern", 
                                     rules.getErrorMessage() != null ? rules.getErrorMessage() : "Invalid format.");
                    }
                    if (rules.getMin() != null) {
                        // Map "minimum" error
                        errorMap.put(field.getKey() + ".minimum", 
                                     "Value must be at least " + rules.getMin());
                    }
                }
                properties.put(field.getKey(), fieldDef);
            }
        }

        jsonSchema.put("properties", properties);
        jsonSchema.put("required", requiredFields);

        // 3. PACKAGE THE PAYLOAD
        Map<String, Object> payload = new HashMap<>();
        payload.put("schemaId", customSchema.getId());
        payload.put("validationSchema", jsonSchema); // The Robot Rules
        payload.put("errorMap", errorMap);           // The Human Rules

        return payload;
    }
}