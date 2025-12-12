package formschema.core.interfaces;

import java.text.Normalizer.Form;

public interface ISchemaParser {
    public String convertToJson(Form form);
}

