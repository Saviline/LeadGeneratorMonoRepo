package formschema.detail.messaging.events;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SchemaBusinessUpdatedEvent extends EventBase{
     private Map<String, String> fieldMappings;
     private Map<String, FieldBusinessRules> businessRules;




    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class FieldBusinessRules {
        private boolean blockFreeProviders;
        private boolean checkMxRecord;
    }
}

