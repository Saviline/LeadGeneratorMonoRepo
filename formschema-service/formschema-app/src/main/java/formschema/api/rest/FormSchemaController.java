package formschema.api.rest;

import org.springframework.web.bind.annotation.*;

import formschema.core.application.FormSchemaService;
import formschema.core.domain.FormSchema;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schemas")
@RequiredArgsConstructor
public class FormSchemaController {

    private final FormSchemaService formSchemaService;

    @PostMapping
    public String createSchema(@RequestBody FormSchema schema) {
        // 1. Spring has already converted JSON -> Java Object

        // 2. You might want to add server-side validation here 
        // (e.g. check that 'mapTo' points to a valid internal field)
        
        // 3. Save to MongoDB
        String id = formSchemaService.createSchema(schema);

        return id;
    }

    // GET /api/schemas/{id}
    @GetMapping("/{id}")
    public FormSchema getSchema(@PathVariable String id) {
        return formSchemaService.getSchemaById(id);
    }

}   