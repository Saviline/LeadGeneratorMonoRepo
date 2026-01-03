package campaign.core.application.exceptions;

public class SchemaNotFoundException extends RuntimeException {

    public SchemaNotFoundException(String message) {
        super(message);
    }
}
