package submission.core.exception;

public class CampaignNotActiveException extends RuntimeException{
    public CampaignNotActiveException(String campaignId){
        super("Campaign not found or not active: " + campaignId);
    }
}
