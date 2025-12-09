package formschema.core.models;

import lombok.Data;

@Data
public class BusinessRules {
    private boolean blockFreeProviders; // true/false
    private boolean checkMxRecord;      // true/false
    // You can add more specific flags here as your system grows
}