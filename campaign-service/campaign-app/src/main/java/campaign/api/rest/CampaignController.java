package campaign.api.rest;

import campaign.core.application.CampaignService;
import campaign.core.application.exceptions.SchemaNotFoundException;
import campaign.core.domain.Campaign;
import campaign.core.domain.FormSchema;
import campaign.core.ports.outbound.IFormSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final IFormSchemaRepository formSchemaRepository;

    @PostMapping
    public Mono<ResponseEntity<Campaign>> createCampaign(@RequestBody Campaign campaign, @AuthenticationPrincipal Jwt jwt) {
        String customerId = jwt.getSubject();
        log.debug("Creating campaign: name={}, customerId={}", campaign.getName(), customerId);

        return campaignService.createCampaign(campaign, customerId)
        .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
        .onErrorResume(SchemaNotFoundException.class, e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/form")
    public Mono<ResponseEntity<FormSchema>> createFormSchema(@RequestBody FormSchema formSchema, @AuthenticationPrincipal Jwt jwt){
        String customerId = jwt.getSubject();
        log.debug("Creating formschema: name={}, customerId={}", formSchema.getId(), customerId);
        return formSchemaRepository.saveFormSchema(formSchema, customerId).map(created -> ResponseEntity.status(HttpStatus.ACCEPTED).body(created));
    }
}
