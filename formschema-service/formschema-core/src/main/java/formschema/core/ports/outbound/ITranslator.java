package formschema.core.ports.outbound;

import formschema.core.domain.FormSchema;

public interface ITranslator<Format> {
    public Format convertToValidationSchema(FormSchema schema);
    public Format convertToBusinessRuleSchema(FormSchema schema);
}

