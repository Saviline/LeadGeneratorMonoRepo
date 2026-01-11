CREATE TABLE IF NOT EXISTS campaigns (
    campaign_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    status VARCHAR(50),
    formschema_id UUID,
    max_submissions INTEGER,
    allow_duplicate_submissions BOOLEAN DEFAULT FALSE,
    require_email_verification BOOLEAN DEFAULT FALSE,
    require_phone_verification BOOLEAN DEFAULT FALSE,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS campaign_integrations (
    campaign_id UUID NOT NULL REFERENCES campaigns(campaign_id) ON DELETE CASCADE,
    integration_id UUID NOT NULL,
    PRIMARY KEY (campaign_id, integration_id)
);
