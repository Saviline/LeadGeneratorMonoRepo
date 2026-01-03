package formschema.detail.messaging.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import formschema.core.domain.FormSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SchemaUpdatedEvent extends EventBase {

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

    public static SchemaUpdatedEvent from(FormSchema schema) {
        List<FormFieldDto> fieldDtos = schema.getFields() != null
            ? schema.getFields().stream()
                .map(field -> {
                    FormFieldDto dto = new FormFieldDto();
                    dto.setKey(field.getKey());
                    dto.setLabel(field.getLabel());
                    dto.setType(field.getType());
                    dto.setMapTo(field.getMapTo());

                    if (field.getValidation() != null) {
                        ValidationRulesDto validationDto = new ValidationRulesDto();
                        validationDto.setRequired(field.getValidation().isRequired());
                        validationDto.setRegex(field.getValidation().getRegex());
                        validationDto.setMin(field.getValidation().getMin());
                        validationDto.setMax(field.getValidation().getMax());
                        validationDto.setErrorMessage(field.getValidation().getErrorMessage());
                        dto.setValidation(validationDto);
                    }

                    return dto;
                })
                .toList()
            : List.of();

        return SchemaUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SCHEMA_UPDATED")
                .timestamp(Instant.now())
                .source("formschema-service")
                .customerId(schema.getCustomerId())
                .schemaId(schema.getId())
                .schemaVersion(schema.getVersion())
                .name(schema.getName())
                .fields(fieldDtos)
                .build();
    }
}
