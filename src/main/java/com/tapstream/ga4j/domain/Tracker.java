package com.tapstream.ga4j.domain;

import com.tapstream.ga4j.Utils;



public class Tracker {

    final private String accountId;
    final private String domainName;
    
    final private CustomVariable[] customVariables = new CustomVariable[5];
    
    private Campaign campaign = null;
    
    public Tracker(String accountId, String domainName){
        this.accountId = accountId;
        this.domainName = domainName;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getDomainName() {
        return domainName;
    }
    
    public int getDomainHash() {
        return Utils.generateHash(domainName);
    }

    public CustomVariable[] getCustomVariables() {
        return customVariables;
    }
    
    public void addCustomVariable(CustomVariable customVariable){
        this.customVariables[customVariable.getIndex()] = customVariable;
    }
    
    public void removeCustomVariable(CustomVariable customVariable){
        this.customVariables[customVariable.getIndex()] = null;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
    
    public boolean hasCampaign() {
        return campaign != null;
    }

}
