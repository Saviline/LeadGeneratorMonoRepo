package campaign.detail.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class EventBase {

    private String eventId;
    private String eventType;
    private Instant timestamp;
    private String source;
    private String customerId;

    public void initializeMetadata(String eventType, String source, String customerId) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = Instant.now();
        this.source = source;
        this.customerId = customerId;
    }
}
