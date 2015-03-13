package com.tapstream.ga4j.domain;

import java.util.ArrayList;
import java.util.List;

import com.tapstream.ga4j.Utils;

public class Cookies {
    
    final public static String UTMA_COOKIE_NAME = "__utma";
    final public static String UTMB_COOKIE_NAME = "__utmb";
    final public static String UTMC_COOKIE_NAME = "__utmc";
    final public static String UTMZ_COOKIE_NAME = "__utmz";
    
    final private String utma;
    final private String utmb;
    final private String utmc;
    final private String utmv;
    final private String utmz;
    
    public Cookies(String utma, String utmb, String utmc, String utmv, String utmz) {
        this.utma = utma;
        this.utmb = utmb;
        this.utmc = utmc;
        this.utmv = utmv;
        this.utmz = utmz;
    }
    
    public String getUtmcc() {
        List<String> utmccCookies = new ArrayList<String>(3);
        utmccCookies.add(String.format("__utma=%s;", utma));
        
        if (utmz != null)
            utmccCookies.add(String.format("__utmz=%s;", utmz));
        
        if (utmv != null)
            utmccCookies.add(String.format("__utmv=%s;", utmv));
        
        return Utils.join(utmccCookies, "+");
    }

    public static Cookies build(Parameters params, Tracker tracker, Visitor visitor, Session session) {
        String utma=null, utmb=null, utmc=null, utmv=null, utmz=null;
        final int domainHash = tracker.getDomainHash();

        utma = buildUtma(domainHash, visitor);
        
        utmb = buildUtmb(domainHash, session);
        
        utmc = buildUtmc(domainHash);
        
        if (params.hasKey("_utmv")) {
            utmv = params.get("_utmv");
        }
                
        if (tracker.getCampaign() != null) {
            utmz = buildUtmz(domainHash, tracker.getCampaign(), visitor);
        }
        
        return new Cookies(utma, utmb, utmc, utmv, utmz);
    }
    
    public static String buildUtma(int domainHash, Visitor visitor) {
        return String.format(
                "%s.%s.%s.%s.%s.%s",
                domainHash,
                visitor.getUniqueId(),
                visitor.getFirstVisitTime(),
                visitor.getPreviousVisitTime(),
                visitor.getCurrentVisitTime(),
                visitor.getVisitCount());
    }
    
    public static String buildUtmb(int domainHash, Session session) {
        return String.format(
                "%s.%s.10.%s",
                domainHash,
                session.getTrackCount(),
                session.getStartTime());
    }
    
    public static String buildUtmc(int domainHash) {
        return String.format("%s", domainHash);
    }
    
    public static String buildUtmz(int domainHash, Campaign campaign, Visitor visitor) {
        return String.format(
                "%s.%s.%s.%s.%s", 
                domainHash, 
                campaign.getCreationTime(),
                visitor.getVisitCount(),
                campaign.getResponseCount(),
                campaign.getOptionalUtmzFragment());
    }

    public String getUtma() {
        return utma;
    }

    public String getUtmb() {
        return utmb;
    }

    public String getUtmc() {
        return utmc;
    }

    public String getUtmv() {
        return utmv;
    }

    public String getUtmz() {
        return utmz;
    }
    
}
