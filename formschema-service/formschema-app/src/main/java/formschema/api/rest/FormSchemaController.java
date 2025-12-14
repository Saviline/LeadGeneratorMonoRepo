package formschema.api.rest;

import org.springframework.web.bind.annotation.*;

import formschema.core.application.FormSchemaService;
import formschema.core.domain.FormSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/schemas")
@RequiredArgsConstructor
public class FormSchemaController {

    private final FormSchemaService formSchemaService;

    @PostMapping
    public String createSchema(@RequestBody FormSchema schema) {
        
        log.debug("FormSchemaController revieced request: schema.name={}, schema.version={}", schema.getName(), schema.getVersion());

        String id = formSchemaService.createSchema(schema);

        return id;
    }

    // GET /api/schemas/{id}
    @GetMapping("/{id}")
    public FormSchema getSchema(@PathVariable String id) {
        return formSchemaService.getSchemaById(id);
    }

}   