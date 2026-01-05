package campaign.api.rest;

import campaign.core.application.CampaignService;
import campaign.core.application.exceptions.SchemaNotFoundException;
import campaign.core.domain.Campaign;
import campaign.core.domain.CampaignStatus;
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

    @PostMapping
    public Mono<ResponseEntity<Campaign>> createCampaign(@RequestBody Campaign campaign, @AuthenticationPrincipal Jwt jwt) {
        String customerId = jwt.getSubject();
        log.debug("Creating campaign: name={}, customerId={}", campaign.getDisplayName(), customerId);

        return campaignService.createCampaign(campaign, customerId)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorResume(SchemaNotFoundException.class, e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping("/{campaignId}/status")
    public Mono<ResponseEntity<Campaign>> updateCampaignStatus(
            @PathVariable String campaignId,
            @RequestBody CampaignStatus status,
            @AuthenticationPrincipal Jwt jwt) {
        String customerId = jwt.getSubject();
        log.debug("Updating campaign status: campaignId={}, status={}, customerId={}", campaignId, status, customerId);

        return campaignService.updateStatus(campaignId, status, customerId)
                .map(updated -> ResponseEntity.ok(updated))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
