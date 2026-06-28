package local.fabricarag.core.dto.worker.components;

public class PrivacyPolicy {
    private Boolean remoteProviderEnabled = false;
    private Boolean includeNearbyText = false;
    private Boolean sendFullPageRender = false;
    private Boolean sendOriginalPdf = false;
    private String historyMode = "metadata_only";

    public Boolean getRemoteProviderEnabled() { return remoteProviderEnabled; }
    public void setRemoteProviderEnabled(Boolean remoteProviderEnabled) { this.remoteProviderEnabled = remoteProviderEnabled; }

    public Boolean getIncludeNearbyText() { return includeNearbyText; }
    public void setIncludeNearbyText(Boolean includeNearbyText) { this.includeNearbyText = includeNearbyText; }

    public Boolean getSendFullPageRender() { return sendFullPageRender; }
    public void setSendFullPageRender(Boolean sendFullPageRender) { this.sendFullPageRender = sendFullPageRender; }

    public Boolean getSendOriginalPdf() { return sendOriginalPdf; }
    public void setSendOriginalPdf(Boolean sendOriginalPdf) { this.sendOriginalPdf = sendOriginalPdf; }

    public String getHistoryMode() { return historyMode; }
    public void setHistoryMode(String historyMode) { this.historyMode = historyMode; }
}
