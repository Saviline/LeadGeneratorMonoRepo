package campaign.detail.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FormSchemaUpdatedEvent extends EventBase {

    private String schemaId;
    private String schemaVersion;
    private String name;
    private List<FormFieldDto> fields;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormFieldDto {
        private String key;
        private String label;
        private String type;
        private String mapTo;
        private ValidationRulesDto validation;
        private BusinessRulesDto businessRules;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationRulesDto {
        private boolean required;
        private String regex;
        private Integer min;
        private Integer max;
        private String errorMessage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessRulesDto {
        private boolean blockFreeProviders;
        private boolean checkMxRecord;
    }
}
