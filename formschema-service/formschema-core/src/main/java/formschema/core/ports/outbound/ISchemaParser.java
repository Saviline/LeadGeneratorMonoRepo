package formschema.core.ports.outbound;

import java.text.Normalizer.Form;

public interface ISchemaParser {
    public String convertToJson(Form form);
}

