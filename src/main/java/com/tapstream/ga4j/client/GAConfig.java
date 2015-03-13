package com.tapstream.ga4j.client;

public class GAConfig {
    
    private String endpoint = "http://www.google-analytics.com/__utm.gif";
    
    private boolean anonymizeIpAddress = true;
    
    private int siteSpeedSampleRate = 1;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isAnonymizeIpAddress() {
        return anonymizeIpAddress;
    }

    public void setAnonymizeIpAddress(boolean anonymizeIpAddress) {
        this.anonymizeIpAddress = anonymizeIpAddress;
    }

    public int getSiteSpeedSampleRate() {
        return siteSpeedSampleRate;
    }

    public void setSiteSpeedSampleRate(int siteSpeedSampleRate) {
        if (siteSpeedSampleRate < 0 || siteSpeedSampleRate > 100)
            throw new IllegalArgumentException("Site speed sample rate must be between 1 and 100");
        this.siteSpeedSampleRate = siteSpeedSampleRate;
    }
    
}
