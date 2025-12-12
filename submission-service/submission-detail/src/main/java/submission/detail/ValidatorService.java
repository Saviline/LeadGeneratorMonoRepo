package submission.detail;

import java.util.Map;

import submission.detail.dto.FieldDefinition;
import submission.detail.dto.FormSchema;
import submission.detail.dto.ValidationException;
import submission.detail.dto.ValidationRules;

public class ValidatorService {

    public void validate(Map<String, Object> submissionData, FormSchema schema) {
        
        // Loop through the RULES (The Schema)
        for (FieldDefinition field : schema.getFields()) {
            
            // 1. Get the actual user input for this field
            Object value = submissionData.get(field.getKey());
            ValidationRules rules = field.getValidation();

            // 2. Check "Required"
            if (rules.isRequired() && (value == null || value.toString().isEmpty())) {
                throw new ValidationException(field.getKey(), rules.getErrorMessage());
            }

            // If value is null and not required, skip other checks
            if (value == null) continue;

            // 3. Check "Regex" (Dynamic Pattern Matching)
            if (rules.getRegex() != null) {
                if (!value.toString().matches(rules.getRegex())) {
                throw new ValidationException(field.getKey(), rules.getErrorMessage());
                }
            }

            // 4. Check "Min/Max" (Numeric Logic)
            if (field.getType().equals("number")) {
                int numVal = Integer.parseInt(value.toString());
                if (rules.getMin() != null && numVal < rules.getMin()) {
                    throw new ValidationException(field.getKey(), rules.getErrorMessage());
                }
            }
        }
    }
}