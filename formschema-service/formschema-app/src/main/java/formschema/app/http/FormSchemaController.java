package formschema.app.http;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import formschema.core.models.FormSchema;
import formschema.core.services.FormSchemaService;
import formschema.core.services.SchemaTranslatorService;
import formschema.detail.rabbitmq.FormSchemaProducer;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schemas")
@RequiredArgsConstructor
public class FormSchemaController {

    private final FormSchemaService formSchemaService;
    private final SchemaTranslatorService SchemaTranslatorService;
    private final FormSchemaProducer formSchemaProducer;

    // POST /api/schemas
    // The Client sends the JSON we designed earlier

    //Routing key
    @Value("${rabbitmq.routingkey.name}")
    String routingKey;

    @Value("${rabbitmq.exchange.name}")
    String exchange;

    @PostMapping
    public String createSchema(@RequestBody FormSchema schema) {
        // 1. Spring has already converted JSON -> Java Object

        // 2. You might want to add server-side validation here 
        // (e.g. check that 'mapTo' points to a valid internal field)
        
        // 3. Save to MongoDB
        String id = formSchemaService.createSchema(schema);

        formSchemaProducer.sendFormSchemaMessage(exchange, routingKey, "New FormSchema created with ID: " + id);

        return id;
    }

    // GET /api/schemas/{id}
    @GetMapping("/{id}")
    public FormSchema getSchema(@PathVariable String id) {
        return formSchemaService.getSchemaById(id);
    }


    @GetMapping("/json/{id}")
    public Map<String, Object> getSubmissionPayload(@PathVariable String id) {
        
        FormSchema schema = formSchemaService.getSchemaById(id);

        return SchemaTranslatorService.generateSubmissionPayload(schema);
    }

}   