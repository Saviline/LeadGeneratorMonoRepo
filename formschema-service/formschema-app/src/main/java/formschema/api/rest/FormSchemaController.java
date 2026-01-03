package formschema.api.rest;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public String createSchema(@RequestBody FormSchema schema, @AuthenticationPrincipal Jwt jwt) {
        String customerId = jwt.getSubject();

        log.debug("FormSchemaController received request: schema.name={}, schema.version={}, customer.id={}",
            schema.getName(), schema.getVersion(), customerId);

        String id = formSchemaService.createSchema(schema, customerId);

        return id;
    }

    @GetMapping("/{id}")
    public FormSchema getSchema(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String customerId = jwt.getSubject();
        return formSchemaService.getSchemaById(id, customerId);
    }

    @GetMapping("/getAll")
    public List<FormSchema> getSchema(@AuthenticationPrincipal Jwt jwt) {
        String customerId = jwt.getSubject();
        return formSchemaService.getAllSchemaByCustomerId(customerId);
    }
}   