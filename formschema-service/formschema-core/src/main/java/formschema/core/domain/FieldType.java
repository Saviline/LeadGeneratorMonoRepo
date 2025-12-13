package formschema.core.domain;

import java.util.Map;

public enum FieldType {
    TEXT {
        @Override
        public void apply(Map<String, Object> fieldDef, ValidationRules rules) {
            fieldDef.put("type", "string");
        }
    },
    NUMBER {
        @Override
        public void apply(Map<String, Object> fieldDef, ValidationRules rules) {
            fieldDef.put("type", "number");
            if (rules != null) {
                if (rules.getMin() != null) fieldDef.put("minimum", rules.getMin());
                if (rules.getMax() != null) fieldDef.put("maximum", rules.getMax());
            }
        }
    },
    EMAIL {
        @Override
        public void apply(Map<String, Object> fieldDef, ValidationRules rules) {
            fieldDef.put("type", "string");
            fieldDef.put("format", "email");
            // Standard Regex for Email to be strict
            fieldDef.put("pattern", "^\\S+@\\S+\\.\\S+$"); 
        }
    };

    // The abstract method that forces every Enum to define its logic
    public abstract void apply(Map<String, Object> fieldDef, ValidationRules rules);
}