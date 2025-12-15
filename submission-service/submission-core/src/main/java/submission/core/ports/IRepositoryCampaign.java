package submission.core.ports;

public interface IRepositoryCampaign {
    void saveCampaign(String CampaignId);
    String getCampaignById(String CampaignId);
}
