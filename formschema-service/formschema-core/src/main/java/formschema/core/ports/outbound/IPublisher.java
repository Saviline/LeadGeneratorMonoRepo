package formschema.core.ports.outbound;

import formschema.core.domain.FormSchema;

public interface IPublisher {

    public void PublishSchema(FormSchema schema);
    
}
